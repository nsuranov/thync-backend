package suranov.thync.controller.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/img")
public class ImageController {
    @Value("${upload.files}")
    private String fileDir;
    @PostMapping
    public Mono<String> handleFileUpload(@RequestPart("file") Mono<Part> filePartMono) {
        return filePartMono
                .ofType(FilePart.class)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.empty();
                })
                .flatMap(filePart -> {
                    String uuid = UUID.randomUUID().toString();
                    String extension = filePart.filename().substring(filePart.filename().lastIndexOf("."));
                    String newFileName = uuid + extension;

                    // Сохраняем файл
                    Path path = Paths.get(fileDir, newFileName);
                    return filePart.transferTo(path.toFile())
                            .then(Mono.just(newFileName));
                });
    }



    @GetMapping("/{filename}")
    public Mono<Resource> downloadFile(@PathVariable String filename) {
        Path path = Paths.get(fileDir, filename);
        Resource resource = new FileSystemResource(path);
        return Mono.just(resource);
    }
}
