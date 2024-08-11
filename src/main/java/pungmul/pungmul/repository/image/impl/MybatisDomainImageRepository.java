package pungmul.pungmul.repository.image.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.dto.file.DomainImageDTO;
import pungmul.pungmul.repository.image.mapper.DomainImageMapper;
import pungmul.pungmul.repository.image.repository.DomainImageRepository;

@Repository
public class MybatisDomainImageRepository implements DomainImageRepository {
    private final DomainImageMapper domainImageMapper;

    public MybatisDomainImageRepository(DomainImageMapper domainImageMapper) {
        this.domainImageMapper = domainImageMapper;
    }
    @Override
    public void save(DomainImageDTO domainImageDTO) {
        domainImageMapper.save(domainImageDTO);
    }
}
