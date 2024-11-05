package pungmul.pungmul.service.file;

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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AWSFileStore extends FileStore {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Override
    public Image saveImageToStorage(Long userId, MultipartFile originImage) throws IOException {
        if (originImage.isEmpty())
            throw new IllegalArgumentException("파일 없음");

        if (originImage.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("파일 크기 초과");

        String originalFilename = originImage.getOriginalFilename();
        String convertedFileName = createFileName(originalFilename);

        // 임시 파일 생성 후 업로드
        File tempFile = File.createTempFile("temp", null);
        originImage.transferTo(tempFile);

        String filePath = "user/" + userId + "/" + convertedFileName;  // 사용자 별 경로로 저장
        putS3(filePath, tempFile);

        Files.delete(tempFile.toPath());  // 임시로 로컬에 저장된 파일 삭제

        return getConvertedImage(userId, originImage, originalFilename, filePath);
    }

    private void putS3(String fileName, File uploadFile) {
        try (FileInputStream inputStream = new FileInputStream(uploadFile)) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(uploadFile));

            if (!response.sdkHttpResponse().isSuccessful()) {
                throw new IllegalStateException("파일 업로드 실패: " + response.sdkHttpResponse().statusText().orElse("Unknown error"));
            }
        } catch (S3Exception | IOException e) {
            throw new IllegalStateException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    private Image getConvertedImage(Long userId, MultipartFile originImage, String originalFilename, String convertedFileName) {
        String fileUrl = generatePresignedUrl(convertedFileName);

        return Image.builder()
                .originalFilename(originalFilename)
                .convertedFileName(convertedFileName)
                .fullFilePath(fileUrl)
                .fileType(originImage.getContentType())
                .fileSize(originImage.getSize())
                .build();
    }

    private String generatePresignedUrl(String key) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region)) // 동일한 리전으로 설정
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(req -> req.bucket(bucketName).key(key))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    @Override
    protected String getFullFilePath(String convertedFileName) {
        return generatePresignedUrl(convertedFileName);
    }
}
