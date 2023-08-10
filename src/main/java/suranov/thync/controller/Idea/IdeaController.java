package suranov.thync.controller.Idea;

import suranov.thync.DTO.Idea.IdeaCreationDto;
import suranov.thync.DTO.Idea.IdeaGetDto;
import suranov.thync.DTO.Idea.IdeaUpdateDto;
import suranov.thync.DTO.Team.CreateTeamDto;
import suranov.thync.domain.Idea.Idea;
import suranov.thync.domain.Team.Team;
import suranov.thync.domain.Utils.Tag;
import suranov.thync.handlers.exceptions.IdeaNotFoundException;
import suranov.thync.service.Idea.IdeaService;
import suranov.thync.service.Team.TeamService;
import suranov.thync.service.Utils.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/idea")
@RequiredArgsConstructor
public class IdeaController {
    private final IdeaService ideaService;
    private final ImageService imageService;
    private final TeamService teamService;
    @PostMapping()
    public Mono<ResponseEntity<Idea>> createIdea(
            @RequestPart(value = "file", required = false) Mono<FilePart> filePartMono,
            @RequestPart("idea") Mono<IdeaCreationDto> ideaCreationDtoMono,
            @RequestHeader("Authorization") String jwt) {

        return filePartMono
                .flatMap(imageService::handleFileUpload)
                .defaultIfEmpty("")
                .flatMap(imageUrl ->
                        ideaCreationDtoMono.flatMap(ideaCreationDto -> {
                            ideaCreationDto.setImgUrl(imageUrl);
                            return ideaService.createIdea(ideaCreationDto, jwt);
                        })
                )
                .map(createdIdea -> ResponseEntity.status(HttpStatus.CREATED).body(createdIdea));
    }



    @GetMapping
    public Mono<Page<IdeaGetDto>> getIdeasWithTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) List<Tag> tags,
            @RequestParam(required = false) String title,
            @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ideaService.getIdeasWithTags(pageable, jwt, tags, title);
    }



    @GetMapping("/{id}")
    public Mono<IdeaGetDto> getIdeaById(@PathVariable String id, @RequestHeader(value = "Authorization", required = false) String jwt) {
        return ideaService.getIdeaWithTags(id, jwt);
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteIdea(@PathVariable String id, @RequestHeader("Authorization") String jwt){
        return ideaService.deleteIdea(id,jwt);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Idea>> updateIdea(@PathVariable String id, @RequestBody IdeaUpdateDto ideaUpdateDto, @RequestHeader("Authorization") String jwt) {
        return ideaService.updateIdea(id,ideaUpdateDto,jwt);
    }

    @PostMapping("/{id}/createteam")
    public Mono<Team> createTeamFromIdea(
           @PathVariable String id,
           @RequestHeader(value = "Authorization") String jwt
    )
    {
        return ideaService.findById(id)
                .flatMap(idea -> {
                    List<String> list = new ArrayList<>();
                    CreateTeamDto createTeamDto = new CreateTeamDto();
                    createTeamDto.setHeader(idea.getHeader());
                    createTeamDto.setDescription(idea.getDescription());
                    createTeamDto.setTags(idea.getTags());
                    createTeamDto.setParticipantsNeeded(true);
                    createTeamDto.setNeeds(list);
                    return teamService.createTeam(createTeamDto,jwt);
                })
                .switchIfEmpty(Mono.error(new IdeaNotFoundException("Idea not found!")));
    }

}
