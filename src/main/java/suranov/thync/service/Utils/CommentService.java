package suranov.thync.service.Utils;

import suranov.thync.DTO.Idea.AddCommentDto;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.service.Idea.IdeaService;
import suranov.thync.service.Utils.ReactionService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final IdeaService ideaService;
    private final ReactionService reactionService;

    public Mono<Comments> addComment(Comments comments, String jwt, AddCommentDto commentDto){
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> {
                    Comment comment = new Comment();
                    comment.setId(UUID.randomUUID().toString());
                    comment.setSenderId(user.getId());
                    comment.setSenderUsername(user.getUsername());
                    comment.setText(commentDto.getText());
                    comment.setSendAt(LocalDateTime.now());
                    comment.setReaction(reactionService.createEmptyReaction());

                    comments.getCommentList().add(comment);

                    return Mono.just(comments);
                });
    }




}
