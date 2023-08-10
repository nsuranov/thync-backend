package suranov.thync.controller.Team;

import suranov.thync.DTO.Idea.IdeaGetDto;
import suranov.thync.DTO.Team.AddArticleInTeam;
import suranov.thync.DTO.Team.CreateTeamDto;
import suranov.thync.DTO.Team.EditContentInTeamDto;
import suranov.thync.DTO.Team.GetTeamDto;
import suranov.thync.domain.Team.Team;
import suranov.thync.domain.Team.TeamStage;
import suranov.thync.service.Team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public Mono<Team> createTeam(
            @RequestBody CreateTeamDto createTeamDto,
            @RequestHeader(value = "Authorization") String jwt
    ){
        return teamService.createTeam(createTeamDto,jwt);
    }

    @PostMapping("/join/{id}")
    public Mono<Team> joinInTeam(
            @PathVariable String id,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.joinInTeam(id, jwt);
    }

    @PostMapping("/{teamId}/confirm/{id}")
    public Mono<Team> confirmInTeam(
            @PathVariable String id,
            @PathVariable String teamId,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.confirmInTeam(teamId, id,jwt);
    }


    @PostMapping("/content/{id}")
    public Mono<Team> createContentElement(
            @PathVariable String id,
            @RequestBody String name,
            @RequestHeader(value = "Authorization") String jwt
    ){
        return teamService.createContentElement(id,name,jwt);
    }

    @PostMapping("/article/{id}")
    public Mono<Team> createArticleInTeam(
            @PathVariable String id,
            @RequestPart("article") AddArticleInTeam articleDto,
            @RequestPart(value = "images", required = false) Flux<FilePart> images,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.createArticleInTeam(id,articleDto,images,jwt);
    }

    @PostMapping("/article/{id}/edit")
    public Mono<Team> editArticleInTeam(
            @PathVariable String id,
            @RequestPart("article") AddArticleInTeam articleDto,
            @RequestPart(value = "images", required = false) Flux<FilePart> images,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.editArticleInTeam(id, articleDto, images, jwt);
    }

    @PostMapping("/content/{id}/edit")
    public Mono<Team> editContentElement(
            @PathVariable String id,
            @RequestBody EditContentInTeamDto contentDto,
            @RequestHeader(value = "Authorization") String jwt
    ){
        return teamService.editContentElement(id,contentDto,jwt);
    }


    @GetMapping("/{id}")
    public Mono<GetTeamDto> getTeamById(
            @PathVariable String id
    )
    {
        return teamService.getTeamById(id);
    }

    @PostMapping("/{id}/changestage")
    public Mono<Team> changeTeamStage(
            @PathVariable String id,
            @RequestBody TeamStage stage,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.changeTeamStage(id,stage,jwt);
    }

    @PostMapping("/{id}/deleteneeds")
    public Mono<Team> deleteNeeds(
            @PathVariable String id,
            @RequestBody List<String> deleteNeed,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.deleteNeeds(id, deleteNeed, jwt);
    }
    @PostMapping("/{id}/addneeds")
    public Mono<Team> addNeeds(
            @PathVariable String id,
            @RequestBody List<String> deleteNeed,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.addNeeds(id, deleteNeed, jwt);
    }

    @PostMapping("/{id}/kickuser")
    public Mono<Team> kickUser(
            @PathVariable String id,
            @RequestBody String userId,
            @RequestHeader(value = "Authorization") String jwt
    )
    {
        return teamService.kickUser(id,userId,jwt);
    }

    @GetMapping()
    public Mono<Page<GetTeamDto>> getPageableTeams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String title,
            @RequestHeader(value = "Authorization", defaultValue = "") String jwt
    ){
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return teamService.getTeamsWithTags(pageable,jwt,tags,title);
    }

    @GetMapping("/stages")
    public Flux<TeamStage> getStages(){
        return Flux.fromArray(TeamStage.values());
    }
}
