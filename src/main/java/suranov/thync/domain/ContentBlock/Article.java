package suranov.thync.domain.ContentBlock;

import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Article {
    @Id
    private String id;
    private String authorId;
    private String authorUsername;
    private String articleTitle;
    private List<Content> contentList;

    private Comments comments;
    private LocalDateTime createAt;

    private Reaction reaction;
}
