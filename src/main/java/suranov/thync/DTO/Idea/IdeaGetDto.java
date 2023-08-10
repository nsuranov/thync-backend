package suranov.thync.DTO.Idea;

import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaGetDto {
    private String creatorUsername;
    private String creatorId;
    private Integer views;
    private String id;
    private String header;
    private String description;
    private String imgUrl;
    private String problem;
    private String ways;
    private Integer likes;
    private Integer dislikes;
    private boolean isLiked;
    private boolean isDisliked;
    private List<Tag> tags;
    private LocalDateTime createAt;
}
