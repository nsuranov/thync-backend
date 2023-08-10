package suranov.thync.domain.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reaction {
    private List<String> likesUsers = new ArrayList<>();
    private List<String> dislikesUsers = new ArrayList<>();
    private Integer likes;
    private Integer dislikes;
}
