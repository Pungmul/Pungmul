package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final DomainImageService domainImageService;
    private final ImageService imageService;

    public void updateProfileImage(Long userId, MultipartFile profile) throws IOException {
        RequestImageDTO imageDTO = RequestImageDTO.builder()
                .domainId(userId)
                .imageFile(profile)
                .userId(userId)
                .domainType(DomainType.PROFILE)
                .build();
        Long imageId = imageService.saveImage(imageDTO);
        domainImageService.updatePrimaryImage(DomainType.PROFILE, userId, imageId);
    }
}

