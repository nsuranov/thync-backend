package suranov.thync.DTO.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {
    private String id;
    private String title;
    private List<ArticleContentDto> articles;
}
