package suranov.thync.DTO.Team;

import suranov.thync.domain.Team.TeamStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamsDto {
    private String id;

    private String header;
    private String description;
    private List<String> tags;
    private LocalDateTime createAt;
    private TeamStage stage;
}
