package suranov.thync.domain.Team;

import suranov.thync.domain.ContentBlock.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String id;
    private String title;
    private List<String> articles;
}
