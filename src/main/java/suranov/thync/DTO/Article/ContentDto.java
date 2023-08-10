package suranov.thync.DTO.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
@Data
@AllArgsConstructor
@NoArgsConstructor


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImageContentDto.class, name = "ImageContent"),
        @JsonSubTypes.Type(value = TextContentDto.class, name = "TextContent")
})
public abstract class ContentDto {
    private Integer order;
}

