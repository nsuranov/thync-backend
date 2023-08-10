package suranov.thync.config;

import suranov.thync.domain.User.User;
import suranov.thync.domain.User.UserRole;
import suranov.thync.domain.Utils.Comment;
import suranov.thync.domain.Utils.Comments;
import suranov.thync.domain.Utils.Reaction;
import suranov.thync.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class InitDB {

    @Bean
    public CommandLineRunner initDatabase(UserRepo repository) {
        return args -> {
            List<String> emptyList = new ArrayList<>();
            List<Comment> emptyComment = new ArrayList<>();
            Reaction reaction = new Reaction(emptyList,emptyList,0,0);
            Comments comments = new Comments(emptyComment);
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setTelegram("");
            admin.setProfile("");
            admin.setAvatarUrl("");
            admin.setComments(comments);
            admin.setIsActivate(true);
            admin.setIdeas(0);
            admin.setReaction(reaction);
            admin.setCreateAt(LocalDateTime.now());
            admin.setMessages(0);
            admin.setEducation("");
            repository.findByUsername("admin").switchIfEmpty(repository.save(admin)).subscribe();
        };
    }
}
