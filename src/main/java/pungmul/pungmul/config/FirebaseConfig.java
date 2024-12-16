package pungmul.pungmul.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FirebaseConfig {
    @Value("${firebase.config.path}")
    private String firebaseAccount;

    @PostConstruct
    public void initializeFirebase() {
        try {
            String resolvedPath = resolveFirebaseConfigPath();
            log.info("Resolved Firebase config path: {}", resolvedPath);

            try (FileInputStream serviceAccount = new FileInputStream(resolvedPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    private String resolveFirebaseConfigPath() {
        File file = new File(firebaseAccount);
        if (file.exists()) {
            // 환경 변수(firebase.config.path)에서 읽은 경로가 유효한 경우
            log.info("Using firebase.config.path from properties: {}", firebaseAccount);
            return firebaseAccount;
        } else {
            // 로컬 IDE 실행 시 경로 분기 처리
            String localPath = "src/main/resources/pungmulsomething-01992001fcf6.json";
            File localFile = new File(localPath);
            if (localFile.exists()) {
                log.info("Using fallback local path: {}", localPath);
                return localPath;
            } else {
                throw new RuntimeException("Firebase config file not found in both firebase.config.path and local fallback path.");
            }
        }
    }
}
