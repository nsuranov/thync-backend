package suranov.thync.controller.User;

import suranov.thync.DTO.User.AuthenticationResponse;
import suranov.thync.DTO.User.LoginDTO;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.User.User;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final static ResponseEntity<Object> UNAUTHORIZED =
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private final UserService userService;
    private final JwtUtil jwtUtil;



    @PostMapping
    public Mono<ResponseEntity<?>> login(@RequestBody LoginDTO loginDTO) {
        return userService.findByUsername(loginDTO.getUsername())
                .cast(User.class)
                .flatMap(userDetails -> {
                    if (Objects.equals(loginDTO.getPassword(), userDetails.getPassword())) {
                        String token = jwtUtil.generateToken(userDetails);
                        AuthenticationResponse response = new AuthenticationResponse();
                        response.setUser(userDetails);
                        response.setJwt(token);
                        return Mono.just(ResponseEntity.ok(response));
                    } else {
                        return Mono.just(UNAUTHORIZED);
                    }
                })
                .defaultIfEmpty(UNAUTHORIZED);
    }

}