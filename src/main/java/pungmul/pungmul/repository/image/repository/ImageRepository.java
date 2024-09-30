package pungmul.pungmul.repository.image.repository;

import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;

import java.util.List;

public interface ImageRepository {

    public void save(Image image);

    public Image getImageByImageId(Long imageId);

    public Image getImageByOriginImageNameAndUserId(String originFileName);

    List<Image> getImagesByImageIds(List<Long> imageIdList);

    List<Image> getImagesByDomainIdAndType(DomainType domainType, Long domainId);

    public Image getImageByConvertedName(String convertedName);
}
