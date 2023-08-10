package suranov.thync.controller.User;


import suranov.thync.DTO.User.AuthenticationResponse;
import suranov.thync.DTO.User.ChangePasswordDTO;
import suranov.thync.DTO.User.CreateUserDTO;
import suranov.thync.DTO.User.UpdateProfileDTO;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.User.User;
import suranov.thync.service.Utils.ImageService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;



    @PostMapping("/create")
    public Mono<User> createUser(
            @RequestPart("user") Mono<CreateUserDTO> userDTOMono,
            @RequestPart(value = "file", required = false) Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(imageService::handleFileUpload)
                .defaultIfEmpty("")
                .flatMap(avatarUrl ->
                        userDTOMono.flatMap(userDTO -> {
                            userDTO.setAvatarUrl(avatarUrl);
                            return userService.createUser(userDTO);
                        })
                );
    }

    @GetMapping()
    public Mono<User> getCurrentUser(@RequestHeader("Authorization") String jwt) {
        String token = jwt.substring(7);
        return userService.findUserByUsername(jwtUtil.extractUsername(token));
    }

    @GetMapping("/{username}")
    public Mono<User> getUser(@PathVariable String username){
        return userService.findUserByUsername(username);
    }


    @PutMapping("/update")
    public Mono<ResponseEntity<AuthenticationResponse>> updateProfile(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Mono<UpdateProfileDTO> profileDTOMono) {

        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> profileDTOMono.flatMap(profileDTO -> userService.updateProfile(user, profileDTO)))
                .flatMap(updatedUser -> {
                    String newToken = jwtUtil.generateToken(updatedUser);
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setUser(updatedUser);
                    response.setJwt(newToken);
                    return Mono.just(ResponseEntity.ok(response));
                });
    }

    @PutMapping("/update-img")
    public Mono<ResponseEntity<AuthenticationResponse>> updateAvatar(
            @RequestHeader("Authorization") String jwt,
            @RequestPart(value = "file") Mono<FilePart> filePartMono) {

        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> filePartMono
                        .flatMap(filePart -> imageService.handleFileUpload(filePart)
                                .doOnNext(url -> {
                                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                                        imageService.deleteFile(user.getAvatarUrl());
                                    }
                                })
                                .flatMap(url -> {
                                    user.setAvatarUrl(url);
                                    return userService.save(user);
                                }))
                        .flatMap(updatedUser -> {
                            String newToken = jwtUtil.generateToken(updatedUser);
                            AuthenticationResponse response = new AuthenticationResponse();
                            response.setUser(updatedUser);
                            response.setJwt(newToken);
                            return Mono.just(ResponseEntity.ok(response));
                        }));

    }






    @PutMapping("/password")
    public Mono<ResponseEntity<AuthenticationResponse>> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePasswordDTO changePasswordDTO) {
        String token = authHeader.substring(7);

        return userService.changePassword(token, changePasswordDTO)
                .flatMap(updatedUser -> {
                    String newToken = jwtUtil.generateToken(updatedUser);
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setUser(updatedUser);
                    response.setJwt(newToken);
                    return Mono.just(ResponseEntity.ok(response));
                });
    }

}
