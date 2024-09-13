package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.Image;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileStore {
    @Value("${imageFile.dir")
    private String imageFileDir;

    public Image saveImageToLocal(Long userId, MultipartFile originImage) throws IOException {
        if (originImage.isEmpty())
             throw new IllegalArgumentException("파일 없음");

        if (originImage.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("파일 크기 초과");

        //  image.png -> 원래 파일명
        String originalFilename = originImage.getOriginalFilename();

        //  저장할 파일명
        String convertedFileName = createFileName(originalFilename);

        //  이미지 로컬 저장
        originImage.transferTo(new File(convertedFileName));

        //  ConvertedImageFile 객체 반환
        return getConvertedImage(userId, originImage, originalFilename, convertedFileName);
    }

    private Image getConvertedImage(Long userId, MultipartFile originImage, String originalFilename, String convertedFileName) {
        return Image.builder()
                    .originalFilename(originalFilename)
                    .convertedFileName(convertedFileName)
                    .fullFilePath(getFullPath(convertedFileName))
                    .fileType(originImage.getContentType())
                    .fileSize(originImage.getSize())
                    .build();
    }

    private String createFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extracted(originalFilename);
        return uuid + "." + ext;
    }

    private String extracted(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String getFullPath(String convertedFileName) {
        return imageFileDir + File.separator + convertedFileName;
    }
}
