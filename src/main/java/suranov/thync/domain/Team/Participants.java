package suranov.thync.domain.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participants {
    private String userId;
    private String username;
    private List<String> role;
    private ParticipantAuthority authority;
}
