package suranov.thync.controller.Utils;

import suranov.thync.DTO.Idea.AddReactionDto;
import suranov.thync.domain.ContentBlock.Article;
import suranov.thync.domain.Idea.Idea;
import suranov.thync.domain.User.User;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.handlers.exceptions.CommentNotFoundException;
import suranov.thync.service.Article.ArticleService;
import suranov.thync.service.Idea.IdeaService;
import suranov.thync.service.Utils.ReactionService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reaction")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;
    private final IdeaService ideaService;
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/idea/{id}")
    public Mono<Idea> addIdeaReaction(
            @PathVariable String id,
            @RequestBody AddReactionDto dto,
            @RequestHeader("Authorization") String jwt
    ){
        return ideaService.findById(id)
                .flatMap(idea ->
                        userService.findById(idea.getCreatorId())
                                .flatMap(user ->
                                        reactionService.addReaction(idea.getReaction(),dto,jwt,user)
                                                .then(ideaService.save(idea))));
    }

    @PostMapping("/idea/{id}/comment/{commentId}")
    public Mono<Idea> addCommentReaction(
            @PathVariable String id,
            @PathVariable String commentId,
            @RequestBody AddReactionDto dto,
            @RequestHeader("Authorization") String jwt
    ){
        return ideaService.findById(id)
                .flatMap(idea -> {
                    Comment comment = idea.getComments().getCommentList().stream()
                            .filter(c -> c.getId().equals(commentId))
                            .findFirst()
                            .orElse(null);
                    if (comment == null) {
                        return Mono.error(new CommentNotFoundException("Comment not found"));
                    }
                    return userService.findById(comment.getSenderId())
                            .flatMap(user ->
                                    reactionService.addReaction(comment.getReaction(), dto, jwt, user)
                                            .thenReturn(idea))
                            .flatMap(ideaService::save);
                });
    }

    @PostMapping("/user/{id}")
    public Mono<User> addUserReaction(
            @PathVariable String id,
            @RequestBody AddReactionDto dto,
            @RequestHeader("Authorization") String jwt
    ){
        return userService.findById(id)
                                .flatMap(user ->
                                        reactionService.addReaction(user.getReaction(),dto,jwt,user)
                                                .then(userService.save(user)));
    }

    @PostMapping("/article/{id}")
    public Mono<Article> addArticleReaction(
            @PathVariable String id,
            @RequestBody AddReactionDto dto,
            @RequestHeader("Authorization") String jwt
    )
    {
        return articleService.getArticleById(id)
                .flatMap(article ->
                        userService.findById(article.getAuthorId())
                                .flatMap(user ->
                                        reactionService.addReaction(article.getReaction(),dto,jwt,user))
                                .then(articleService.save(article))
                        );
    }

}

