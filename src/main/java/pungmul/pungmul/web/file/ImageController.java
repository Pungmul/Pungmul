package pungmul.pungmul.web.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.loginvalidation.user.User;

import java.io.IOException;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upload/chat")
    public ResponseEntity<Image> addImage(
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        try {
            Long userId = userRepository.getUserIdByAccountId(userDetails.getAccountId());
            RequestImageDTO requestImageDTO = new RequestImageDTO(userId, DomainType.CHAT, file);
            Image image = imageService.saveImage(requestImageDTO);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
