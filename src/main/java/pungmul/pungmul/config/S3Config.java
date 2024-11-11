package pungmul.pungmul.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.accessKey}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsSecretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.key-path}")
    private String keyPath;

//    private final Path keystorePath = Paths.get(System.getProperty("java.io.tmpdir"), "keystore.p12");

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsAccessKey,
                awsSecretKey
        );

        return S3Client.builder()
                .region(Region.of(region))  // AWS 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

//    @PostConstruct
//    public void downloadKeystore() {
//        try {
//            if (Files.notExists(keystorePath)) {
//                // S3에서 인증서를 다운로드
//                s3Client().getObject(
//                        GetObjectRequest.builder()
//                                .bucket(bucketName)
//                                .key(keyPath)
//                                .build(),
//                        keystorePath
//                );
//                System.out.println("Keystore downloaded to: " + keystorePath.toString());
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to download keystore from S3: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

//    @Bean
//    public Path keystorePath() {
//        return keystorePath;
//    }
}
