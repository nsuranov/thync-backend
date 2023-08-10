package suranov.thync.repo.Article;

import suranov.thync.domain.ContentBlock.Article;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArticleRepo extends ReactiveCrudRepository<Article,String> {
}
