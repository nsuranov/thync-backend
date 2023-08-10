package suranov.thync.service.Utils;


import suranov.thync.DTO.Idea.AddReactionDto;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.User.User;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.domain.Utils.ReactionEnum;
import suranov.thync.handlers.exceptions.IncorrectOldPasswordException;
import suranov.thync.handlers.exceptions.SameUserReactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public Reaction createEmptyReaction(){
        List<String> empty = new ArrayList<>();
        Reaction reaction = new Reaction(empty,empty,0,0);
        return reaction;
    }

    public Mono<Reaction> addReaction(Reaction reaction, AddReactionDto dto, String jwt, User ownerUser) {
        String token = jwt.substring(7);
        if(token.equals("null")){
            return Mono.error(new IncorrectOldPasswordException("Incorrect token"));
        }
        String username = jwtUtil.extractUsername(token);
        Reaction ownerReaction = ownerUser.getReaction();

        if (username.equals(ownerUser.getUsername())) {
            return Mono.error(new SameUserReactionException("User cannot react to own content"));
        }

        return userService.findUserByUsername(username)
                .flatMap(user -> {
                    if(dto.getReaction().equals(ReactionEnum.LIKE)){
                        if(reaction.getLikesUsers().contains(user.getId())){
                            reaction.getLikesUsers().remove(user.getId());
                            if(reaction != ownerReaction){
                                ownerReaction.setLikes(ownerReaction.getLikes() - 1);
                            }

                            reaction.setLikes(reaction.getLikes() - 1);
                        } else {
                            if (reaction.getDislikesUsers().contains(user.getId())) {
                                reaction.getDislikesUsers().remove(user.getId());
                                if(reaction != ownerReaction) {
                                    ownerReaction.setDislikes(ownerReaction.getDislikes() - 1);
                                }
                                reaction.setDislikes(reaction.getDislikes() - 1);
                            }
                            reaction.getLikesUsers().add(user.getId());
                            if(reaction != ownerReaction) {
                                ownerReaction.setLikes(ownerReaction.getLikes() + 1);
                            }
                            reaction.setLikes(reaction.getLikes() + 1);
                        }
                    } else if (dto.getReaction().equals(ReactionEnum.DISLIKE)){
                        if(reaction.getDislikesUsers().contains(user.getId())){
                            reaction.getDislikesUsers().remove(user.getId());
                            if(reaction != ownerReaction) {
                                ownerReaction.setDislikes(ownerReaction.getDislikes() - 1);
                            }
                            reaction.setDislikes(reaction.getDislikes() - 1);
                        } else {
                            if (reaction.getLikesUsers().contains(user.getId())) {
                                reaction.getLikesUsers().remove(user.getId());
                                if(reaction != ownerReaction) {
                                    ownerReaction.setLikes(ownerReaction.getLikes() - 1);
                                }
                                reaction.setLikes(reaction.getLikes() - 1);
                            }
                            reaction.getDislikesUsers().add(user.getId());
                            if(reaction != ownerReaction){
                                ownerReaction.setDislikes(ownerReaction.getDislikes() + 1);
                            }
                            reaction.setDislikes(reaction.getDislikes() + 1);
                        }
                    }
                    return userService.save(ownerUser).thenReturn(reaction);
                });
    }

}


