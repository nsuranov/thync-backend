package suranov.thync.DTO.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TextContentDto extends ContentDto {
    private String text;
    private boolean header;
    private boolean subheader;
    private boolean code;
}
