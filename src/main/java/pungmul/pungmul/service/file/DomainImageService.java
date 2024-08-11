package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.DomainImageDTO;
import pungmul.pungmul.repository.image.repository.DomainImageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainImageService {
    private final DomainImageRepository domainImageRepository;

    public void saveDomainImage(DomainType domainType, Long domainId, Long imageId){
        log.info("{}",domainId);
        DomainImageDTO domainImageDTO = DomainImageDTO.builder()
                .domainType(domainType)
                .domainId(domainId)
                .imageId(imageId)
//                .isPrimary(isPrimary)
                .build();

        domainImageRepository.save(domainImageDTO);
    }


}
