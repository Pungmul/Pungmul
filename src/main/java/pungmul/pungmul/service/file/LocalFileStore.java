package pungmul.pungmul.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.Image;

import java.io.File;
import java.io.IOException;

@Component
public class LocalFileStore extends FileStore {

    @Value("${imageFile.dir.local}")
    private String imageFileDir;

    @Override
    public Image saveImageToStorage(Long userId, MultipartFile originImage) throws IOException {
        if (originImage.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
        String originalFilename = originImage.getOriginalFilename();
        String convertedFileName = createFileName(originalFilename);

        // 로컬 파일 저장
        File file = new File(getFullFilePath(convertedFileName));
        originImage.transferTo(file);

        // 이미지 객체 반환
        return getConvertedImage(userId, originImage, originalFilename, convertedFileName);
    }

    @Override
    protected String getFullFilePath(String convertedFileName) {
        // 로컬 파일 경로 반환
        return imageFileDir + File.separator + convertedFileName;
    }

    private Image getConvertedImage(Long userId, MultipartFile originImage, String originalFilename, String convertedFileName) {
        return Image.builder()
                .originalFilename(originalFilename)
                .convertedFileName(convertedFileName)
                .fullFilePath(getFullFilePath(convertedFileName))
                .fileType(originImage.getContentType())
                .fileSize(originImage.getSize())
                .build();
    }
}