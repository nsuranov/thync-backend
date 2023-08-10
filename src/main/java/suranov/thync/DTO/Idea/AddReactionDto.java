package suranov.thync.DTO.Idea;

import suranov.thync.domain.Utils.ReactionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddReactionDto {
    private ReactionEnum reaction;
}

