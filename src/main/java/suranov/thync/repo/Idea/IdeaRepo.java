package suranov.thync.repo.Idea;

import suranov.thync.domain.Idea.Idea;
import suranov.thync.domain.Utils.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IdeaRepo extends ReactiveCrudRepository<Idea,String> {
    Flux<Idea> findIdeasBy(Pageable pageable);

    Mono<Idea> findById(String id);
    Mono<Void> deleteById(String id);
    Flux<Idea> findByHeaderContaining(String header, Pageable pageable);
    Flux<Idea> findByTagsIn(List<Tag> tags, Pageable pageable);

    Flux<Idea> findByCreatorId(String creatorId);
}
