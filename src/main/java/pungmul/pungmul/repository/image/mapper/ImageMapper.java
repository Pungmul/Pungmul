package pungmul.pungmul.repository.image.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.file.Image;

@Mapper
public interface ImageMapper {
    void save(Image image);

    Image getImageByImageId(Long id);

    Image getImageByOriginImageNameAndUserId(@Param("userId") Long userId,@Param("originFileName") String imageName);

}
