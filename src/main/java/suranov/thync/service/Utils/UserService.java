package suranov.thync.service.Utils;

import suranov.thync.DTO.User.ChangePasswordDTO;
import suranov.thync.DTO.User.CreateUserDTO;
import suranov.thync.DTO.User.UpdateProfileDTO;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.User.User;
import suranov.thync.domain.User.UserRole;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.handlers.exceptions.IncorrectOldPasswordException;
import suranov.thync.handlers.exceptions.UserAlreadyExistsException;
import suranov.thync.handlers.exceptions.UserNotFoundException;
import suranov.thync.handlers.exceptions.UsernameTokenMismatchException;
import suranov.thync.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUsername(username)
                .cast(UserDetails.class);
    }
    private Reaction createEmptyReaction(){
        List<String> empty = new ArrayList<>();
        Reaction reaction = new Reaction(empty,empty,0,0);
        return reaction;
    }
    public Mono<User> findById(String id){
        return userRepo.findById(id);
    }
    public Mono<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Mono<User> save(User user){
        return userRepo.save(user);
    }

    public Mono<Boolean> existsByUsername(String username){
        return userRepo.existsByUsername(username);
    }

    public String passwordEncode(String password){
        return password;
    }

    public Mono<User> saveDTO(CreateUserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncode(userDTO.getPassword()));
        user.setRole(UserRole.ROLE_NEW);
        user.setIsActivate(true);
        user.setMessages(0);
        user.setReaction(createEmptyReaction());
        user.setIdeas(0);
        user.setAvatarUrl(userDTO.getAvatarUrl());
        user.setEducation(userDTO.getEducation());
        user.setTelegram(userDTO.getTelegram());
        user.setProfile(userDTO.getProfile());
        user.setCreateAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    public Mono<User> createUser(CreateUserDTO userDTO) {
        return userRepo.findByUsername(userDTO.getUsername())
                .flatMap(existingUser -> Mono.<User>error(new UserAlreadyExistsException("User already exists")))
                .switchIfEmpty(saveDTO(userDTO));
    }

    public Mono<User> changePassword(String token, ChangePasswordDTO changePasswordDTO) {
        if(token.equals("null")){
            return Mono.error(new IncorrectOldPasswordException("Incorrect token"));
        }
        String usernameFromToken = jwtUtil.extractUsername(token);

        if (!changePasswordDTO.getUsername().equals(usernameFromToken)) {
            return Mono.error(new UsernameTokenMismatchException("Username does not match the one in the token"));
        }
        return userRepo.findByUsername(changePasswordDTO.getUsername())
                .flatMap(user -> {
                    if (Objects.equals(user.getPassword(), passwordEncode(changePasswordDTO.getOldPassword()))) {
                        user.setPassword(passwordEncode(changePasswordDTO.getNewPassword()));
                        return userRepo.save(user);
                    } else {
                        return Mono.error(new IncorrectOldPasswordException("Old password does not match"));
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
    }

    public Mono<User> updateProfile(User user, UpdateProfileDTO profileDTO) {
        user.setTelegram(profileDTO.getTelegram());
        user.setEducation(profileDTO.getEducation());
        user.setProfile(profileDTO.getProfile());
        return userRepo.save(user);
    }

    public Flux<User> getAll() {
        return userRepo.findAll();
    }
}
