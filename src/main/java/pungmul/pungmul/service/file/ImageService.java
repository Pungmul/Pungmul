package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.image.repository.ImageRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    // FileStore 대신 AWSFileStore를 주입받음
    private final AWSFileStore awsFileStore;
    private final ImageRepository imageRepository;
    private final DomainImageService domainImageService;

    @Value("${image.anonymous_profile.name}")
    private String anonymousProfileName;

    public void saveImage(RequestImageDTO requestImageDTO) throws IOException {
        // AWSFileStore의 saveImageToLocal 메소드를 호출하여 이미지를 AWS S3에 저장
        Image image = awsFileStore.saveImageToStorage(requestImageDTO.getUserId(), requestImageDTO.getImageFile());
        saveImageToRepo(requestImageDTO.getDomainId(), requestImageDTO.getDomainType(), image, requestImageDTO.getUserId());
    }

    private void saveImageToRepo(Long domainId, DomainType domainType, Image image, Long userId) {
        imageRepository.save(image);
        domainImageService.saveDomainImage(domainType, domainId, image.getId(), userId);
    }

    public List<Image> getImagesByIdList(List<Long> imageIdList) {
        if (imageIdList == null || imageIdList.isEmpty()) {
            return Collections.emptyList();
        }
        return imageRepository.getImagesByImageIds(imageIdList);
    }

    public Image getImageById(Long imageId) {
        return imageRepository.getImageByImageId(imageId);
    }


    public List<Image> getImagesByDomainId(DomainType domainType, Long domainId) {
        List<Image> images = imageRepository.getImagesByDomainIdAndType(domainType, domainId);
//        log.info(images.size() + " images found");

        // 기본 이미지 설정
        if (images.isEmpty()) {
            // 기본 이미지 리스트로 대체
            if (domainType == DomainType.PROFILE)
                return Collections.singletonList(imageRepository.getImageByConvertedName(anonymousProfileName));
            else
                throw new NoSuchElementException();
        }
        return images;
    }
}
