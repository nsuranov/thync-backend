package suranov.thync.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Document
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    private UserRole role;

    private Boolean isActivate;

    private LocalDateTime createAt;
    private Integer ideas;
    private Integer messages;
    private String telegram;
    private String education;
    private String profile;
    private String avatarUrl;
    private Reaction reaction;
    private Comments comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActivate;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActivate;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActivate;
    }
}
