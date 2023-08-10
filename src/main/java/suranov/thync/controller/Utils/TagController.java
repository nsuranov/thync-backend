package suranov.thync.controller.Utils;

import suranov.thync.domain.Utils.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/api/tag")
public class TagController {
    @GetMapping
    public Flux<Tag> getTags() {
        return Flux.fromArray(Tag.values());
    }
}
