package suranov.thync.DTO.Team;

import suranov.thync.domain.Team.Participants;
import suranov.thync.domain.Team.TeamStage;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamDto {
    private String id;

    private String header;
    private String description;
    private List<Tag> tags;
    private LocalDateTime createAt;
    private TeamStage stage;

    private boolean participantsNeeded;
    private List<Participants> participants;
    private List<Participants> requests;
    private List<String> needs;

    private List<ContentDto> content;

    private Comments comments;
}
