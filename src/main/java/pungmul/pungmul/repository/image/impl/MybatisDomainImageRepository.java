package pungmul.pungmul.repository.image.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.file.DomainImage;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.DomainImageDTO;
import pungmul.pungmul.repository.image.mapper.DomainImageMapper;
import pungmul.pungmul.repository.image.repository.DomainImageRepository;

import java.util.List;

@Repository
public class MybatisDomainImageRepository implements DomainImageRepository {
    private final DomainImageMapper domainImageMapper;

    public MybatisDomainImageRepository(DomainImageMapper domainImageMapper) {
        this.domainImageMapper = domainImageMapper;
    }
    @Override
    public void save(DomainImage domainImage) {
        domainImageMapper.save(domainImage);
    }

    @Override
    public List<DomainImage> getDomainImagesByDomainId(DomainType domainType, Long domainId) {
        return domainImageMapper.getDomainImageByDomainId(domainType, domainId);
    }
}
