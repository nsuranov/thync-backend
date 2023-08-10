package suranov.thync.domain.Idea;

import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Idea {
    @Id
    private String id;

    private String header;
    private String description;
    private String imgUrl;
    private String creatorId;
    private String problem;
    private String ways;
    private Integer views;
    private List<Tag> tags;
    private Comments comments;
    private LocalDateTime createAt;

    private Reaction reaction;


}
