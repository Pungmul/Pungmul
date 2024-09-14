package pungmul.pungmul.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.Image;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


@Component
@RequiredArgsConstructor
public class AWSFileStore extends FileStore {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Override
    public Image saveImageToStorage(Long userId, MultipartFile originImage) throws IOException {
        if (originImage.isEmpty())
            throw new IllegalArgumentException("파일 없음");

        if (originImage.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("파일 크기 초과");

        String originalFilename = originImage.getOriginalFilename();
        String convertedFileName = createFileName(originalFilename);

        // S3에 파일 업로드
        File tempFile = File.createTempFile("temp", null);
        originImage.transferTo(tempFile);

        String filePath = "user/" + userId + "/" + convertedFileName;  // 사용자 별 경로로 저장
        putS3(filePath, tempFile);

        Files.delete(tempFile.toPath());  // 임시로 로컬에 저장된 파일 삭제

        return getConvertedImage(userId, originImage, originalFilename, filePath);
    }

    private void putS3(String fileName, File uploadFile) {
        try {
            amazonS3.putObject(bucketName, fileName, uploadFile);
        } catch (AmazonS3Exception e) {
            throw new IllegalStateException("파일 업로드 실패: " + e.getMessage());
        }
    }

    private Image getConvertedImage(Long userId, MultipartFile originImage, String originalFilename, String convertedFileName) {
        // Amazon S3에서 해당 파일의 URL을 가져옴
        String fileUrl = amazonS3.getUrl(bucketName, convertedFileName).toString();

        return Image.builder()
                .originalFilename(originalFilename)
                .convertedFileName(convertedFileName)
                .fullFilePath(fileUrl)
                .fileType(originImage.getContentType())
                .fileSize(originImage.getSize())
                .build();
    }

    @Override
    protected String getFullFilePath(String convertedFileName) {
        return amazonS3.getUrl(bucketName, convertedFileName).toString();
    }

}
