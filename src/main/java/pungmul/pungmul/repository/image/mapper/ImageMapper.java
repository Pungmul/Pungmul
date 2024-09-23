package pungmul.pungmul.repository.image.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;

import java.util.List;

@Mapper
public interface ImageMapper {
    void save(Image image);

    Image getImageByImageId(Long id);

    Image getImageByOriginImageNameAndUserId(String imageName);

    List<Image> getImagesByImageIds(List<Long> imageIdList);

    List<Image> getImagesByDomainIdAndType(DomainType domainType, Long domainId);
}
