package suranov.thync.service.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${upload.files}")
    private String fileDir;
    public Mono<String> handleFileUpload(FilePart filePart) {
        String uuid = UUID.randomUUID().toString();
        String extension = filePart.filename().substring(filePart.filename().lastIndexOf("."));
        String newFileName = uuid + extension;

        Path path = Paths.get(fileDir, newFileName);

        return filePart.transferTo(path.toFile())
                .then(Mono.just(newFileName));
    }


    public Mono<Void> deleteFile(String filename) {
        try {
            Path filePath = Paths.get(fileDir, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {

        }
        return Mono.empty();
    }

}
