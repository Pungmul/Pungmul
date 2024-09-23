package pungmul.pungmul.repository.image.repository;

import pungmul.pungmul.domain.file.DomainImage;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.DomainImageDTO;

import java.util.List;

public interface DomainImageRepository {
    void save(DomainImage domainImage);

    List<DomainImage> getDomainImagesByDomainId(DomainType domainType, Long domainId);
}
