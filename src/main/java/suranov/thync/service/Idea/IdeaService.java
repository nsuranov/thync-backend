package suranov.thync.service.Idea;

import suranov.thync.DTO.Idea.IdeaCreationDto;
import suranov.thync.DTO.Idea.IdeaGetDto;
import suranov.thync.DTO.Idea.IdeaUpdateDto;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Idea.Idea;
import suranov.thync.domain.User.User;
import suranov.thync.domain.User.UserRole;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.domain.Utils.Tag;
import suranov.thync.handlers.exceptions.IdeaNotFoundException;
import suranov.thync.handlers.exceptions.UsernameTokenMismatchException;
import suranov.thync.repo.Idea.IdeaRepo;
import suranov.thync.service.Utils.ImageService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdeaService {
    private final IdeaRepo ideaRepo;
    private final JwtUtil jwtUtil;
    private final  UserService userService;
    private final ImageService imageService;


    public Mono<Idea> createIdea(IdeaCreationDto ideaCreationDto, String jwt) {
        List<String> emptyList = new ArrayList<>();
        List<Comment> emptyComment = new ArrayList<>();
        Reaction reaction = new Reaction(emptyList,emptyList,0,0);
        Comments comments = new Comments(emptyComment);
        Idea idea = new Idea();
        idea.setHeader(ideaCreationDto.getHeader());
        idea.setDescription(ideaCreationDto.getDescription());
        idea.setImgUrl(ideaCreationDto.getImgUrl());
        idea.setProblem(ideaCreationDto.getProblem());
        idea.setWays(ideaCreationDto.getWays());
        idea.setReaction(reaction);
        idea.setViews(0);
        idea.setTags(ideaCreationDto.getTags());
        idea.setComments(comments);
        idea.setCreateAt(LocalDateTime.now());
        String token = jwt.substring(7);
        Mono<User> creator = userService.findUserByUsername(jwtUtil.extractUsername(token));

        return creator.flatMap(user -> {
            idea.setCreatorId(user.getId());
            user.setIdeas(user.getIdeas() + 1);
            if(user.getRole().equals(UserRole.ROLE_NEW)){
                user.setRole(UserRole.ROLE_USER);
            }
            return userService.save(user)
                    .then(ideaRepo.save(idea));
        });
    }



    public Mono<Page<IdeaGetDto>> getIdeasWithTags(Pageable pageable, String jwt, List<Tag> tagsFromSearch, String name) {
        Flux<Idea> ideasFlux;
        if (tagsFromSearch == null || tagsFromSearch.isEmpty()) {
            if (name == null || name.isEmpty()) {
                ideasFlux = ideaRepo.findIdeasBy(pageable);
            } else {
                ideasFlux = ideaRepo.findByHeaderContaining(name, pageable);
            }
        } else {
            ideasFlux = getIdeasContainsTags(tagsFromSearch, pageable);
            if (name != null && !name.isEmpty()) {
                ideasFlux = ideasFlux.filter(idea -> idea.getHeader().contains(name));
            }
        }
        return ideasFlux.flatMap(idea -> {
                    IdeaGetDto dto = new IdeaGetDto();
                    dto.setId(idea.getId());
                    dto.setHeader(idea.getHeader());
                    dto.setDescription(idea.getDescription());
                    dto.setImgUrl(idea.getImgUrl());
                    dto.setProblem(idea.getProblem());
                    dto.setWays(idea.getWays());
                    dto.setLikes(idea.getReaction().getLikes());
                    dto.setDislikes(idea.getReaction().getDislikes());
                    dto.setViews(idea.getViews());
                    dto.setTags(idea.getTags());
                    dto.setCreateAt(idea.getCreateAt());
                    return userService.findById(idea.getCreatorId())
                            .flatMap(user -> {
                                dto.setCreatorUsername(user.getUsername());
                                dto.setCreatorId(user.getId());
                                if (!jwt.isEmpty() && !jwt.isBlank()) {
                                    String token = jwt.substring(7);
                                    return userService.findUserByUsername(jwtUtil.extractUsername(token))
                                            .flatMap(currentUser -> {
                                               String userId = currentUser.getId();
                                               dto.setLiked(idea.getReaction().getLikesUsers().contains(userId));
                                               dto.setDisliked(idea.getReaction().getDislikesUsers().contains(userId));
                                               return Mono.just(dto);
                                            });
                                } else {
                                    dto.setLiked(false);
                                    dto.setDisliked(false);
                                    return Mono.just(dto);
                                }

                            });
                })
                .collectList()
                .flatMap(ideas -> ideaRepo.count()
                        .map(total -> new PageImpl<>(ideas, pageable, total))
                );
    }

    private Flux<Idea> getIdeasContainsTags(List<Tag> tags, Pageable pageable) {
        return ideaRepo.findByTagsIn(tags, pageable)
                .filter(idea -> idea.getTags().containsAll(tags))
                .distinct();
    }





    public Mono<Idea> findById(String id) {
        return ideaRepo.findById(id);
    }
    public Mono<Void> deleteById(String id) {
        return ideaRepo.deleteById(id);
    }

    public Mono<Idea> save(Idea idea) {
        return ideaRepo.save(idea);
    }

    public Mono<ResponseEntity<?>> deleteIdea(String id, String jwt) {
        String token = jwt.substring(7);
        Mono<User> creator = userService.findUserByUsername(jwtUtil.extractUsername(token));

        return creator.flatMap(user -> findById(id)
                .flatMap(idea -> {
                    if (Objects.equals(idea.getCreatorId(), user.getId()) || UserRole.ROLE_ADMIN.equals(user.getRole())) {
                        return imageService.deleteFile(idea.getImgUrl())
                                .then(deleteById(id))
                                .thenReturn(ResponseEntity.ok(idea));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                }).switchIfEmpty(Mono.error(new IdeaNotFoundException("Idea not found"))));
    }

    public Mono<ResponseEntity<Idea>> updateIdea(String id, IdeaUpdateDto ideaUpdateDto, String jwt) {
        String token = jwt.substring(7);
        Mono<User> creator = userService.findUserByUsername(jwtUtil.extractUsername(token));

        return creator.flatMap(user -> {
            return findById(id)
                    .flatMap(idea -> {
                        if (Objects.equals(idea.getCreatorId(), user.getId()) || UserRole.ROLE_ADMIN.equals(user.getRole())) {
                            idea.setHeader(ideaUpdateDto.getHeader());
                            idea.setDescription(ideaUpdateDto.getDescription());
                            idea.setImgUrl(ideaUpdateDto.getImgUrl());
                            idea.setProblem(ideaUpdateDto.getProblem());
                            idea.setWays(ideaUpdateDto.getWays());
                            idea.setTags(ideaUpdateDto.getTags());
                            return save(idea)
                                    .map(ResponseEntity::ok);
                        } else {
                            return Mono.error(new UsernameTokenMismatchException("You can only edit your own ideas"));
                        }
                    }).switchIfEmpty(Mono.error(new IdeaNotFoundException("Idea not found")));
        }).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    public Mono<IdeaGetDto> getIdeaWithTags(String id, String jwt) {
        return ideaRepo.findById(id)
                .switchIfEmpty(Mono.error(new IdeaNotFoundException("Idea not found")))
                .flatMap(idea -> {
                    idea.setViews(idea.getViews() + 1);
                    return ideaRepo.save(idea)
                            .map(savedIdea -> {
                                IdeaGetDto dto = new IdeaGetDto();
                                dto.setId(savedIdea.getId());
                                dto.setHeader(savedIdea.getHeader());
                                dto.setDescription(savedIdea.getDescription());
                                dto.setImgUrl(savedIdea.getImgUrl());
                                dto.setViews(savedIdea.getViews());
                                dto.setProblem(savedIdea.getProblem());
                                dto.setWays(savedIdea.getWays());
                                dto.setLikes(savedIdea.getReaction().getLikes());
                                dto.setDislikes(savedIdea.getReaction().getDislikes());
                                dto.setTags(savedIdea.getTags());
                                dto.setCreateAt(idea.getCreateAt());
                                return dto;
                            })
                            .flatMap(dto -> userService.findById(idea.getCreatorId())
                                    .flatMap(user -> {
                                        dto.setCreatorUsername(user.getUsername());
                                        dto.setCreatorId(user.getId());
                                        if (jwt != null && !jwt.isEmpty()) {
                                            String token = jwt.substring(7);
                                            return userService.findUserByUsername(jwtUtil.extractUsername(token))
                                                    .flatMap(currentUser -> {
                                                        String currentUserId = currentUser.getId();
                                                        dto.setLiked(idea.getReaction().getLikesUsers().contains(currentUserId));
                                                        dto.setDisliked(idea.getReaction().getDislikesUsers().contains(currentUserId));
                                                        return Mono.just(dto);

                                                    });
                                        } else {
                                            dto.setLiked(false);
                                            dto.setDisliked(false);
                                            return Mono.just(dto);
                                        }
                                    }));
                });
    }

    public Flux<Idea> getUserIdeas(String userId){
        return ideaRepo.findByCreatorId(userId);
    }

}


