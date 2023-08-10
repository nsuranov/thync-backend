package suranov.thync.domain.ContentBlock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextContent extends Content{
    private String text;
    private boolean header;
    private boolean subheader;
    private boolean code;
    public TextContent(Integer order, String text, boolean header, boolean subheader, boolean code) {
        super(order);
        this.text = text;
        this.header = header;
        this.subheader = subheader;
        this.code = code;
    }
}
