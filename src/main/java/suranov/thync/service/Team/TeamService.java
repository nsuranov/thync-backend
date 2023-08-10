package suranov.thync.service.Team;


import suranov.thync.DTO.Team.*;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.Team.*;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.handlers.exceptions.CustomException;
import suranov.thync.handlers.exceptions.TeamAlreadyExistException;
import suranov.thync.handlers.exceptions.TeamNotFoundException;
import suranov.thync.handlers.exceptions.UserNotFoundException;
import suranov.thync.repo.Team.TeamRepo;
import suranov.thync.service.Article.ArticleService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepo teamRepo;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ArticleService articleService;

    public Mono<Team> save(Team team){
        return teamRepo.save(team);
    }
    public Mono<Team> findTeamById(String id){
        return teamRepo.findById(id);
    }

    public Mono<Team> createTeam(CreateTeamDto createTeamDto, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return teamRepo.findTeamByHeader(createTeamDto.getHeader())
                .flatMap(team -> Mono.<Team>error(new TeamAlreadyExistException("Team with this header already exists!")))
                .switchIfEmpty(Mono.defer(() ->
                        userService.findUserByUsername(username)
                                .flatMap(user -> {
                                    Team team = new Team();
                                    List<Participants> participantsEmptyList = new ArrayList<>();
                                    List<Participants> participantsList = new ArrayList<>();
                                    List<String> creatorRoles = new ArrayList<>();
                                    creatorRoles.add(user.getProfile());
                                    Participants creator = new Participants(
                                            user.getId(),
                                            user.getUsername(),
                                            creatorRoles,
                                            ParticipantAuthority.OWNER
                                    );
                                    participantsList.add(creator);
                                    team.setHeader(createTeamDto.getHeader());
                                    team.setDescription(createTeamDto.getDescription());
                                    team.setTags(createTeamDto.getTags());
                                    team.setCreateAt(LocalDateTime.now());

                                    team.setNeeds(createTeamDto.getNeeds());
                                    team.setParticipantsNeeded(createTeamDto.isParticipantsNeeded());
                                    team.setStage(createTeamDto.isParticipantsNeeded() ? TeamStage.GET_PARTICIPANTS : TeamStage.MAIN_STAGE);
                                    team.setParticipants(participantsList);
                                    team.setRequests(participantsEmptyList);

                                    List<Comment> emptyComment = new ArrayList<>();
                                    Comments comments = new Comments(emptyComment);
                                    team.setComments(comments);

                                    List<Content> contentList = new ArrayList<>();
                                    List<String> articleList = new ArrayList<>();
                                    Content content = new Content();
                                    return articleService.generateStartArticle(createTeamDto,user)
                                            .flatMap(article ->{
                                                 content.setId(UUID.randomUUID().toString());
                                                 content.setTitle(createTeamDto.getHeader());
                                                 articleList.add(article.getId());
                                                 content.setArticles(articleList);
                                                 contentList.add(content);
                                                 team.setContentList(contentList);
                                                 return teamRepo.save(team);
                                            }
                                            );
                                })
                ));
    }


    public Mono<Team> joinInTeam(String id, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return teamRepo.findById(id)
                .switchIfEmpty(Mono.error(new TeamNotFoundException("Team with this id not exists!")))
                .flatMap(team ->
                        userService.findUserByUsername(username)
                                .flatMap(user -> {
                                    boolean userExists = team.getParticipants().stream()
                                            .anyMatch(participant -> participant.getUserId().equals(user.getId()));
                                    boolean userExistsInWait = team.getRequests().stream()
                                            .anyMatch(request -> request.getUserId().equals(user.getId()));
                                    if(userExists || userExistsInWait){
                                        return Mono.error(new UserNotFoundException("User with username " + username + " already in team"));
                                    } else {
                                        List<String> userRoles = new ArrayList<>();
                                        userRoles.add(user.getProfile());
                                        Participants participant = new Participants(
                                                user.getId(),
                                                user.getUsername(),
                                                userRoles,
                                                ParticipantAuthority.MEMBER
                                        );
                                        team.getRequests().add(participant);
                                        return teamRepo.save(team);
                                    }

                                })
                                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username " + username + " not found!")))
                );
    }

    public Mono<Team> confirmInTeam(String teamId, String id, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> teamRepo.findById(teamId)
                        .flatMap(team -> {
                            if (team.getParticipants().stream().anyMatch(participant -> participant.getUserId().equals(user.getId()))) {
                                Participants participant = team.getRequests().stream()
                                        .filter(request -> request.getUserId().equals(id))
                                        .findAny()
                                        .orElse(null);

                                if (participant != null) {
                                    team.getRequests().remove(participant);
                                    team.getParticipants().add(participant);
                                    return teamRepo.save(team);
                                } else {
                                    return Mono.error(new  UserNotFoundException("User with id \" + id + \" not found in requests"));
                                }
                            } else {
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant of the team"));
                            }
                        })
                        .switchIfEmpty(Mono.error(new TeamNotFoundException("Team with id " + teamId + " not found"))));
    }




    public Mono<Team> createContentElement(String id, String name, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userService.findUserByUsername(username)
                .flatMap(user ->
                        teamRepo.findById(id)
                                .flatMap(team -> {
                                    boolean isUserHasAuthorities = team.getParticipants().stream()
                                            .anyMatch(participants -> participants.getUserId().equals(user.getId()));
                                    if (isUserHasAuthorities){
                                        List<String> articleList = new ArrayList<>();
                                        return articleService.generateEmptyArticle(name,user)
                                                        .flatMap(article -> {
                                                            articleList.add(article.getId());
                                                            Content newContent = new Content(
                                                                    UUID.randomUUID().toString(),
                                                                    name,
                                                                    articleList
                                                                    );
                                                            List<Content> contentList = team.getContentList();
                                                            contentList.add(newContent);
                                                            return teamRepo.save(team);
                                                        });

                                    }else {
                                        return Mono.error(new CustomException("User haven`t authorities for it!"));
                                    }
                                })
                                .switchIfEmpty(Mono.error(new TeamNotFoundException("Team with id " + id + " not found")))
                        )
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id \" + id + \" not found in requests")));
    }

    private Content findContentById(List<Content> contentList, String id) {
        for (Content content : contentList) {
            if (content.getId().equals(id)) {
                return content;
            }
        }
        return null;
    }

    public Mono<Team> createArticleInTeam(String id, AddArticleInTeam articleDto, Flux<FilePart> images, String jwt) {
        return executeWithTeamAndUser(id, jwt, team -> {
            List<Content> contentList = team.getContentList();
            Content parentContent = findContentById(contentList, articleDto.getParent());

            if (parentContent == null) {
                return Mono.error(new CustomException("Parent Content not found!"));
            } else {
                return images.collectList().flatMap(imageList ->
                        articleService.createArticle(articleDto.getArticleDto(), imageList, jwt)
                                .flatMap(article -> {
                                    List<String> articleList = parentContent.getArticles();
                                    articleList.add(article.getId());
                                    return teamRepo.save(team);
                                })
                );
            }
        });
    }


    public Mono<GetTeamDto> getTeamById(String id) {
        return teamRepo.findById(id)
                .flatMap(team -> {
                    GetTeamDto getTeamDto = new GetTeamDto();
                    getTeamDto.setId(team.getId());
                    getTeamDto.setHeader(team.getHeader());
                    getTeamDto.setDescription(team.getDescription());
                    getTeamDto.setTags(team.getTags());
                    getTeamDto.setCreateAt(team.getCreateAt());
                    getTeamDto.setStage(team.getStage());
                    getTeamDto.setParticipantsNeeded(team.isParticipantsNeeded());
                    getTeamDto.setParticipants(team.getParticipants());
                    getTeamDto.setRequests(team.getRequests());
                    getTeamDto.setNeeds(team.getNeeds());
                    getTeamDto.setComments(team.getComments());

                    return Flux.fromIterable(team.getContentList())
                            .flatMap(content -> {
                                ContentDto contentDto = new ContentDto();
                                contentDto.setId(content.getId());
                                contentDto.setTitle(content.getTitle());

                                return Flux.fromIterable(content.getArticles())
                                        .flatMap(articleId -> articleService.getArticleById(articleId)
                                                .map(article -> {
                                                    ArticleContentDto articleContentDto = new ArticleContentDto();
                                                    articleContentDto.setId(article.getId());
                                                    articleContentDto.setTitle(article.getArticleTitle());
                                                    return articleContentDto;
                                                }))
                                        .collectList()
                                        .map(articleContentDtos -> {
                                            contentDto.setArticles(articleContentDtos);
                                            return contentDto;
                                        });
                            })
                            .collectList()
                            .map(contentDtos -> {
                                getTeamDto.setContent(contentDtos);
                                return getTeamDto;
                            });
                })
                .switchIfEmpty(Mono.error(new TeamNotFoundException("Team with id " + id + " not found")));
    }

    public Mono<Team> editArticleInTeam(String id, AddArticleInTeam articleDto, Flux<FilePart> images, String jwt) {
        return executeWithTeamAndUser(id, jwt, team -> {
            List<Content> contentList = team.getContentList();
            Content parentContent = findContentById(contentList, articleDto.getParent());

            if (parentContent == null) {
                return Mono.error(new CustomException("Parent Content not found!"));
            } else {
                return images.collectList().flatMap(imageList ->
                        articleService.editArticle(articleDto, imageList, jwt)
                                .flatMap(article -> {
                                    return teamRepo.save(team);
                                })
                );
            }
        });
    }


    public Mono<Team> editContentElement(String id, EditContentInTeamDto contentDto, String jwt) {
        return executeWithTeamAndUser(id, jwt, team -> {
            List<Content> contentList = team.getContentList();
            Optional<Content> contentToEditOptional = contentList.stream()
                    .filter(content -> content.getId().equals(contentDto.getId()))
                    .findFirst();

            if(contentToEditOptional.isPresent()) {
                Content contentToEdit = contentToEditOptional.get();
                contentToEdit.setTitle(contentDto.getTitle());
                return teamRepo.save(team);
            } else {
                return Mono.error(new CustomException("Content not found!"));
            }
        });
    }


    private Mono<Team> executeWithTeamAndUser(String id, String jwt, Function<Team, Mono<Team>> func) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> teamRepo.findById(id)
                        .flatMap(team -> {
                            boolean isUserHasAuthorities = team.getParticipants().stream()
                                    .anyMatch(participants -> participants.getUserId().equals(user.getId()));
                            if (isUserHasAuthorities) {
                                return func.apply(team);
                            } else {
                                return Mono.error(new CustomException("User hasn`t authorities for it!"));
                            }
                        })
                        .switchIfEmpty(Mono.error(new TeamNotFoundException("Team with id " + id + " not found")))
                )
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id \" + id + \" not found in requests")));
    }


    public Mono<Team> changeTeamStage(String id, TeamStage stage, String jwt) {
        return executeWithTeamAndUser(id, jwt, team -> {
            team.setStage(stage);
            return teamRepo.save(team);
        });
    }

    public Mono<Team> deleteNeeds(String id, List<String> deleteNeed, String jwt) {
        return executeWithTeamAndUser(id,jwt, team ->{
           List<String> oldNeeds = team.getNeeds();
            oldNeeds.removeAll(deleteNeed);
            return teamRepo.save(team);
        });
    }

    public Mono<Team> addNeeds(String id, List<String> addNeed, String jwt) {
        return executeWithTeamAndUser(id,jwt, team ->{
            List<String> oldNeeds = team.getNeeds();
            oldNeeds.addAll(addNeed);
            return teamRepo.save(team);
        });
    }

    public Mono<Team> kickUser(String id, String userId, String jwt) {
        return executeWithTeamAndUser(id,jwt, team ->{
            List<Participants> participantsList = team.getParticipants();
            // найти Participant с указанным id и удалить его из команды
            participantsList.removeIf(participant -> participant.getUserId().equals(userId));
            team.setParticipants(participantsList);
            return teamRepo.save(team);
        });
    }

    public Mono<Page<GetTeamDto>> getTeamsWithTags(Pageable pageable, String jwt, List<String> tagsFromSearch, String title) {
        Flux<Team> teamFlux;
        if (tagsFromSearch == null || tagsFromSearch.isEmpty()) {
            if (title == null || title.isEmpty()) {
                teamFlux = teamRepo.findTeamsBy(pageable);
            } else {
                teamFlux = teamRepo.findByHeaderContaining(title, pageable);
            }
        } else {
            teamFlux = getTeamsContainsTags(tagsFromSearch, pageable);
            if (title != null && !title.isEmpty()) {
                teamFlux = teamFlux.filter(team -> team.getHeader().contains(title));
            }
        }
        return teamFlux.flatMap(team -> this.getTeamById(team.getId()))  // use getTeamById to convert each team to DTO
                .collectList()
                .flatMap(teams -> teamRepo.count()
                        .map(total -> new PageImpl<>(teams, pageable, total))
                );
    }

    private Flux<Team> getTeamsContainsTags(List<String> tags, Pageable pageable) {
        return teamRepo.findByTagsIn(tags, pageable)
                .filter(team -> team.getTags().containsAll(tags))
                .distinct();
    }

}
