package suranov.thync.controller.Utils;

import suranov.thync.DTO.Idea.AddCommentDto;
import suranov.thync.domain.ContentBlock.Article;
import suranov.thync.domain.Idea.Idea;
import suranov.thync.domain.Team.Team;
import suranov.thync.domain.User.User;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.service.Article.ArticleService;
import suranov.thync.service.Team.TeamService;
import suranov.thync.service.Utils.CommentService;
import suranov.thync.service.Idea.IdeaService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final IdeaService ideaService;
    private final CommentService commentService;
    private final UserService userService;
    private final TeamService teamService;
    private final ArticleService articleService;
    @GetMapping("/idea/{id}")
    public Mono<Comments> getIdeaComments(
            @PathVariable String id
    )
    {
        return ideaService.findById(id).flatMap(idea -> Mono.just(idea.getComments()));
    }

    @PostMapping("/idea/{id}")
    public Mono<Idea> addCommentToIdea(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String id,
            @RequestBody AddCommentDto commentDto
            )
    {
        return ideaService.findById(id).flatMap(idea ->
                commentService.addComment(idea.getComments(), jwt, commentDto)
                        .then(ideaService.save(idea))
        );
    }

    @GetMapping("/user/{id}")
    public Mono<Comments> getUserComments(
            @PathVariable String id
    )
    {
        return userService.findById(id).flatMap(user-> Mono.just(user.getComments()));
    }
    @PostMapping("/user/{id}")
    public Mono<User> addCommentToUser(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String id,
            @RequestBody AddCommentDto commentDto
    )
    {
        return userService.findById(id).flatMap(user ->
                commentService.addComment(user.getComments(), jwt, commentDto)
                        .then(userService.save(user))
        );
    }
    @GetMapping("/team/{id}")
    public Mono<Comments> getTeamComments(
            @PathVariable String id
    )
    {
        return teamService.findTeamById(id).flatMap(team-> Mono.just(team.getComments()));
    }
    @PostMapping("/team/{id}")
    public Mono<Team> addCommentToTeam(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String id,
            @RequestBody AddCommentDto commentDto
    )
    {
        return teamService.findTeamById(id)
                .flatMap(team ->
                   commentService.addComment(team.getComments(),jwt,commentDto).then(teamService.save(team))
                );
    }

    @GetMapping("/article/{id}")
    public Mono<Comments> getArticleComments(
            @PathVariable String id
    )
    {
        return articleService.getArticleById(id).flatMap(article -> Mono.just(article.getComments()));
    }

    @PostMapping("/article/{id}")
    public Mono<Article> addCommentToArticle(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String id,
            @RequestBody AddCommentDto commentDto
    )
    {
        return articleService.getArticleById(id)
                .flatMap(article ->
                        commentService.addComment(article.getComments(),jwt,commentDto).then(articleService.save(article))
                );
    }

}
