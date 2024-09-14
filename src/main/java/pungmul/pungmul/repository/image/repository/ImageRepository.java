package pungmul.pungmul.repository.image.repository;

import pungmul.pungmul.domain.file.Image;

public interface ImageRepository {

    public void save(Image image);

    public Image getImageByImageId(Long imageId);

    public Image getImageByOriginImageNameAndUserId(String originFileName);

}
