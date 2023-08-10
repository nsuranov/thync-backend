package suranov.thync.controller.Admin;

import suranov.thync.domain.User.User;
import suranov.thync.handlers.exceptions.UserNotFoundException;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    @GetMapping("/users")
    public Flux<User> getUsers(){
        return userService.getAll();
    }

    @PostMapping("/ban/{id}")
    public Mono<User> banUser(
            @PathVariable String id
    )
    {
        return userService.findById(id).flatMap(
                user -> {
                    user.setIsActivate(!(user.getIsActivate()));
                    return userService.save(user);
                }
        ).switchIfEmpty(Mono.error(new UserNotFoundException("User not found!")));
    }


}
