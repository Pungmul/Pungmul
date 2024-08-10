package pungmul.pungmul.web.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.service.file.FileStore;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class FileController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Image> addImage(@ModelAttribute RequestImageDTO requestImageDTO) throws IOException {
        return ResponseEntity.ok(imageService.saveImage(requestImageDTO));
    }
}
