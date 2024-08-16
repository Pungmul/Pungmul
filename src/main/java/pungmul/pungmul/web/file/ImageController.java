package pungmul.pungmul.web.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.loginvalidation.user.User;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload/chat")
    public ResponseEntity<Image> addImage(
                                    @User SessionUser sessionUser,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        try {
            RequestImageDTO requestImageDTO = new RequestImageDTO(sessionUser.getUserId(), DomainType.CHAT, file);
            Image image = imageService.saveImage(requestImageDTO);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
