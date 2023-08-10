package suranov.thync.DTO.Idea;

import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaUpdateDto {
    private String header;
    private String description;
    private String imgUrl;
    private String problem;
    private String ways;
    private List<Tag> tags;
}

