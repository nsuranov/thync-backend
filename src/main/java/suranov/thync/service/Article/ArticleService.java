package suranov.thync.service.Article;

import suranov.thync.DTO.Article.ArticleDto;
import suranov.thync.DTO.Article.ContentDto;
import suranov.thync.DTO.Article.ImageContentDto;
import suranov.thync.DTO.Article.TextContentDto;
import suranov.thync.DTO.Team.AddArticleInTeam;
import suranov.thync.DTO.Team.CreateTeamDto;
import suranov.thync.config.JwtUtil;
import suranov.thync.domain.ContentBlock.Article;
import suranov.thync.domain.ContentBlock.Content;
import suranov.thync.domain.ContentBlock.ImageContent;
import suranov.thync.domain.ContentBlock.TextContent;
import suranov.thync.domain.User.User;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.handlers.exceptions.ArticleNotFoundException;
import suranov.thync.repo.Article.ArticleRepo;
import suranov.thync.service.Utils.ImageService;
import suranov.thync.service.Utils.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepo articleRepo;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    public Mono<Article> save(Article article){
        return articleRepo.save(article);
    }

    public Mono<Article> generateEmptyArticle(String title, User user){
        Article article = new Article();
        article.setArticleTitle(title);
        article.setAuthorId(user.getId());
        article.setAuthorUsername(user.getUsername());
        article.setCreateAt(LocalDateTime.now());

        List<Comment> emptyComment = new ArrayList<>();
        Comments comments = new Comments(emptyComment);
        article.setComments(comments);

        List<Content> content = new ArrayList<>();
        TextContent textContent = new TextContent(
                1,
                title,
                true,
                false,
                false
        );
        content.add(textContent);
        article.setContentList(content);
        List<String> emptyList = new ArrayList<>();
        Reaction reaction = new Reaction(
                emptyList,
                emptyList,
                0,
                0
        );
        article.setReaction(reaction);
        return articleRepo.save(article);
    }

    public Mono<Article> generateStartArticle(CreateTeamDto dto, User user){
        Article article = new Article();
        article.setArticleTitle(dto.getHeader());
        article.setAuthorId(user.getId());
        article.setAuthorUsername(user.getUsername());
        article.setCreateAt(LocalDateTime.now());

        List<Comment> emptyComment = new ArrayList<>();
        Comments comments = new Comments(emptyComment);
        article.setComments(comments);

        List<Content> content = new ArrayList<>();
        TextContent textContent = new TextContent(
                1,
                dto.getDescription(),
                true,
                false,
                false
        );
        content.add(textContent);
        article.setContentList(content);
        List<String> emptyList = new ArrayList<>();
        Reaction reaction = new Reaction(
                emptyList,
                emptyList,
                0,
                0
        );
        article.setReaction(reaction);
        return articleRepo.save(article);
    }
    public Mono<Article> createArticle(ArticleDto articleDto, List<FilePart> images, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user ->{

                    Article article = new Article();
                    article.setAuthorId(user.getId());
                    article.setAuthorUsername(user.getUsername());
                    article.setArticleTitle(articleDto.getArticleTitle());


                    List<Content> contentList = new ArrayList<>();
                    List<Mono<Content>> contentMonos = new ArrayList<>();

                    for(ContentDto contentDto: articleDto.getContentList()){
                        if(contentDto instanceof TextContentDto) {
                            TextContentDto textContentDto = (TextContentDto) contentDto;
                            Mono<Content> contentMono = Mono.just(new TextContent(
                                    textContentDto.getOrder(),
                                    textContentDto.getText(),
                                    textContentDto.isHeader(),
                                    textContentDto.isSubheader(),
                                    textContentDto.isCode()));
                            contentMonos.add(contentMono);
                        } else {
                            ImageContentDto imageContentDto = (ImageContentDto) contentDto;

                            FilePart image = images.stream()
                                    .filter(img -> img.filename().equals(imageContentDto.getImgUrl()))
                                    .findFirst()
                                    .orElse(null);

                            if (image != null) {
                                Mono<Content> contentMono = imageService.handleFileUpload(image)
                                        .map(filename -> new ImageContent(imageContentDto.getOrder(), filename));
                                contentMonos.add(contentMono);
                            } else {
                                contentMonos.add(Mono.just(new ImageContent(imageContentDto.getOrder(), imageContentDto.getImgUrl())));
                            }
                        }
                    }
                    List<Comment> emptyComment = new ArrayList<>();
                    Comments comments = new Comments(emptyComment);
                    article.setComments(comments);
                    List<String> emptyList = new ArrayList<>();
                    Reaction reaction = new Reaction(
                            emptyList,
                            emptyList,
                            0,
                            0
                    );
                    article.setReaction(reaction);
                    article.setCreateAt(LocalDateTime.now());
                    return Flux.concat(contentMonos).collectList().doOnNext(article::setContentList)
                            .thenReturn(article);

                })
                .flatMap(articleRepo::save);
    }




    public Mono<Article> getArticleById(String id){
        return articleRepo.findById(id)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article with id: "+id+" not found!")));
    }


    public Mono<Article> editArticle(AddArticleInTeam articleDto, List<FilePart> imageList, String jwt) {
        String token = jwt.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userService.findUserByUsername(username)
                .flatMap(user -> articleRepo.findById(articleDto.getArticleId())
                        .flatMap(article -> {
                            article.setArticleTitle(articleDto.getArticleDto().getArticleTitle());

                            List<Mono<Content>> contentMonos = new ArrayList<>();

                            for(ContentDto contentDto: articleDto.getArticleDto().getContentList()){
                                if(contentDto instanceof TextContentDto) {
                                    TextContentDto textContentDto = (TextContentDto) contentDto;
                                    Mono<Content> contentMono = Mono.just(new TextContent(
                                            textContentDto.getOrder(),
                                            textContentDto.getText(),
                                            textContentDto.isHeader(),
                                            textContentDto.isSubheader(),
                                            textContentDto.isCode()));
                                    contentMonos.add(contentMono);
                                } else {
                                    ImageContentDto imageContentDto = (ImageContentDto) contentDto;

                                    FilePart image = imageList.stream()
                                            .filter(img -> img.filename().equals(imageContentDto.getImgUrl()))
                                            .findFirst()
                                            .orElse(null);

                                    if (image != null) {
                                        Mono<Content> contentMono = imageService.handleFileUpload(image)
                                                .map(filename -> new ImageContent(imageContentDto.getOrder(), filename));
                                        contentMonos.add(contentMono);
                                    } else {
                                        contentMonos.add(Mono.just(new ImageContent(imageContentDto.getOrder(), imageContentDto.getImgUrl())));
                                    }
                                }
                            }

                            return Flux.concat(contentMonos).collectList().doOnNext(article::setContentList)
                                    .thenReturn(article);
                        })
                        .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article with id"+articleDto.getArticleId()+ "not found")))
                )
                .flatMap(articleRepo::save);
    }

}
