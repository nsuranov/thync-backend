package suranov.thync.domain.Team;

import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Team {
    @Id
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

    private List<Content> contentList;

    private Comments comments;
}
