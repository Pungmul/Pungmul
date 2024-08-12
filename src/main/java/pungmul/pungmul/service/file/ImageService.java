package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.image.repository.ImageRepository;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileStore fileStore;
    private final ImageRepository imageRepository;
    private final DomainImageService domainImageService;

    public Image saveImage(RequestImageDTO requestImageDTO) throws IOException {
        log.info("userId : {}, Saving image {}", requestImageDTO.getUserId(), requestImageDTO.getImageFile());
        Image image = fileStore.saveImageToLocal(requestImageDTO.getUserId(), requestImageDTO.getImageFile());

        return saveImageToRepo(requestImageDTO.getUserId(),
                                requestImageDTO.getDomainType(),
                                image);
    }

    private Image saveImageToRepo(Long userId, DomainType domainType, Image image) {
        imageRepository.save(image);
        domainImageService.saveDomainImage(domainType, userId, image.getId());
        return imageRepository.getImageByImageId(image.getId());
    }
}
