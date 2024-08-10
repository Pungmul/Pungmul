package pungmul.pungmul.repository.image.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.repository.image.mapper.ImageMapper;
import pungmul.pungmul.repository.image.repository.ImageRepository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisImageRepository implements ImageRepository {
    private final ImageMapper imageMapper;

    @Override
    public void save(Image image) {
        imageMapper.save(image);
    }

    @Override
    public Image getImageByImageId(Long imageId) {
        return imageMapper.getImageByImageId(imageId);
    }

    @Override
    public Image getImageByOriginImageNameAndUserId(Long userId, String originFileName) {
        return imageMapper.getImageByOriginImageNameAndUserId(userId, originFileName);
    }


}
