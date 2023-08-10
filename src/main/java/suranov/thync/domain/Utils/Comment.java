package suranov.thync.domain.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String id;
    private String senderId;
    private String senderUsername;
    private String text;
    private LocalDateTime sendAt;
    private Reaction reaction;
}
