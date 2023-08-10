package suranov.thync.DTO.Team;

import suranov.thync.DTO.Article.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddArticleInTeam {
    private ArticleDto articleDto;
    private String articleId;
    private String parent;
}
