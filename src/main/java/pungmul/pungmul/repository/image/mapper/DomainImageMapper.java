package pungmul.pungmul.repository.image.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.dto.file.DomainImageDTO;

@Mapper
public interface DomainImageMapper {
    void save(DomainImageDTO domainImageDTO);
}
