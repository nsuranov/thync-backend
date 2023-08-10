package suranov.thync.domain.ContentBlock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageContent extends Content {
    private String imgUrl;

    public ImageContent(Integer order, String imgUrl) {
        super(order);
        this.imgUrl = imgUrl;
    }

}
