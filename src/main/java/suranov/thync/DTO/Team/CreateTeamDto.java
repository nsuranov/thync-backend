package suranov.thync.DTO.Team;

import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamDto {
    private String header;
    private String description;
    private List<Tag> tags;
    private boolean participantsNeeded;
    private List<String> needs;
}
