package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainImage;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.DomainImageDTO;
import pungmul.pungmul.repository.image.repository.DomainImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainImageService {
    private final DomainImageRepository domainImageRepository;

    public void saveDomainImage(DomainType domainType, Long domainId, Long imageId, Long userId){
        DomainImage domainImage = getDomainImage(domainType, domainId, imageId, userId);
        domainImageRepository.save(domainImage);
    }

    private DomainImage getDomainImage(DomainType domainType, Long domainId, Long imageId, Long userId) {
        return DomainImage.builder()
                    .domainId(domainId)
                    .domainType(domainType)
                    .userId(userId)
                    .imageId(imageId)
                    .build();
    }

    public DomainImageDTO getDomainImageDTO(DomainImage domainImage) {
        return DomainImageDTO.builder()
                .domainType(domainImage.getDomainType())
                .domainId(domainImage.getDomainId())
                .imageId(domainImage.getImageId())
                .userId(domainImage.getUserId())
                .build();
    }


    public List<DomainImageDTO> getDomainImagesByDomainId(DomainType domainType, Long domainId) {
        List<DomainImage> domainImagesByDomainId = domainImageRepository.getDomainImagesByDomainId(domainType, domainId);

        // Stream API를 이용해 리스트 변환
        return domainImagesByDomainId.stream()
                .map(this::getDomainImageDTO)
                .collect(Collectors.toList());
    }

    public void updatePrimaryImage(DomainType domainType, Long domainId, Long imageId) {
        domainImageRepository.deactivateAllPrimaryImages(domainType, domainId);
        domainImageRepository.activatePrimaryImage(domainType, domainId, imageId);
    }
}
