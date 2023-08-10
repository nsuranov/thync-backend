package suranov.thync.controller.Article;

import suranov.thync.DTO.Article.ArticleDto;
import suranov.thync.domain.ContentBlock.Article;
import suranov.thync.service.Article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Article> createArticle(
            @RequestHeader("Authorization") String jwt,
            @RequestPart("article") Mono<ArticleDto> articleDtoMono,
            @RequestPart("images") Flux<FilePart> images
    ) {
        return articleDtoMono
                .zipWith(images.collectList(), Tuples::of)
                .flatMap(tuple -> articleService.createArticle(tuple.getT1(), tuple.getT2(), jwt));
    }


    @GetMapping("/{id}")
    public Mono<Article> getArticleById(@PathVariable String id){
        return articleService.getArticleById(id);
    }
}
