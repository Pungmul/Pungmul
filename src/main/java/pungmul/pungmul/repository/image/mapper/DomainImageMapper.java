package pungmul.pungmul.repository.image.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.file.DomainImage;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.DomainImageDTO;

import java.util.List;

@Mapper
public interface DomainImageMapper {
    void save(DomainImage domainImage);

    List<DomainImage> getDomainImageByDomainId(DomainType domainType, Long domainId);

}
