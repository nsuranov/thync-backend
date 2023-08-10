package suranov.thync.repo;

import suranov.thync.domain.User.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveCrudRepository<User, String> {
    Mono<User> findByUsername(String name);
    Mono<User> findById(String id);


    Mono<Boolean> existsByUsername(String username);
}
