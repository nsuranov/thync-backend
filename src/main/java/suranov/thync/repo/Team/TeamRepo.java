package suranov.thync.repo.Team;

import suranov.thync.domain.Team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TeamRepo extends ReactiveCrudRepository<Team,String> {
    Mono<Team> findTeamByHeader(String header);
    Mono<Team> save(Team team);

    Flux<Team> findByTagsIn(List<String> tags, Pageable pageable);

    Flux<Team> findByHeaderContaining(String title, Pageable pageable);

    Flux<Team> findTeamsBy(Pageable pageable);
}
