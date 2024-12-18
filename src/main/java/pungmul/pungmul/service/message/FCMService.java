package pungmul.pungmul.service.message;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.domain.message.NotificationContent;
import pungmul.pungmul.dto.message.FCMTokenRequestDTO;
import pungmul.pungmul.dto.message.UpdateFCMTokenDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.message.repository.FCMRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {
    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/{project_id}/messages:send";

    private final UserRepository userRepository;
    private final FCMRepository fcmRepository;

    @Value("${firebase.config.path}")
    private String firebaseAccount;

    @Value("${firebase.config.project-id}")
    private String firebaseProjectId;

    private static final String LOCAL_FCM_CONFIG_PATH =
            "src/main/resources/pungmulsomething-01992001fcf6.json";

    // Access Token 생성
    private String getAccessToken() throws IOException {
        String effectivePath = resolveFirebaseAccountPath();

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(effectivePath))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * 파일 경로를 확인해 AWS 경로 또는 로컬 경로를 사용
     */
    private String resolveFirebaseAccountPath() {
        File awsFile = new File(firebaseAccount);
        if (awsFile.exists()) {
            return firebaseAccount; // AWS 서버 경로 사용
        } else {
            // 로컬 경로 확인
            if (Files.exists(Paths.get(LOCAL_FCM_CONFIG_PATH))) {
                return LOCAL_FCM_CONFIG_PATH;
            } else {
                throw new RuntimeException("Firebase account file not found in both AWS and Local paths");
            }
        }
    }

    // FCM 메시지 전송
    public void sendNotification(String token, NotificationContent notificationContent) throws IOException {
        String message = createMessage(token, notificationContent.getTitle(), notificationContent.getBody());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(message, headers);
        String response = restTemplate.postForObject(
                FCM_API_URL.replace("{project_id}", firebaseProjectId),
                request,
                String.class
        );
//        System.out.println("Response from FCM: " + response);
    }

    // 메시지 생성
    private String createMessage(String token, String title, String body) {
        return """
            {
                "message": {
                    "token": "%s",
                    "notification": {
                        "title": "%s",
                        "body": "%s"
                    }
                }
            }
            """.formatted(token, title, body);
    }

    @Transactional
    public void saveFCMToken(UserDetailsImpl userDetails, FCMTokenRequestDTO fcmTokenRequestDTO) {
        FCMToken fcmToken = FCMToken.builder()
                .userId(userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new))
                .token(fcmTokenRequestDTO.getFcmToken())
                .deviceInfo(fcmTokenRequestDTO.getDeviceInfo())
                .isValid(true)
                .build();
        fcmRepository.saveOrUpdateToken(fcmToken);
    }

    public List<String> getValidTokens(Long userId) {
        // 유효한 토큰만 필터링하여 반환
        return fcmRepository.findTokensByUserId(userId).stream()
                .filter(FCMToken::isValid) // isValid 필드가 true인 것만 필터링
                .map(FCMToken::getToken)  // FCMToken 객체에서 token 값만 추출
                .toList();
    }

    public void invalidateToken(String token) {
        fcmRepository.updateTokenValidity(UpdateFCMTokenDTO.builder().token(token).isValid(false).build());
    }

    // 2. Find FCM Tokens by User ID
    public List<String> getTokensByUserId(Long userId) {
        List<FCMToken> tokens = fcmRepository.findTokensByUserId(userId);
        log.info("Retrieved {} tokens for userId: {}", tokens.size(), userId);
        return tokens.stream().map(FCMToken::getToken).toList();
    }

    // 3. Invalidate a Token
    @Transactional
    public void invalidateFCMToken(String token) {
        fcmRepository.invalidateToken(token);
        log.info("Invalidated FCM token: {}", token);
    }

    // 4. Get All Valid Tokens
    public List<String> getAllValidTokens() {
        List<FCMToken> validTokens = fcmRepository.getValidTokens();
        log.info("Retrieved {} valid tokens", validTokens.size());
        return validTokens.stream().map(FCMToken::getToken).toList();
    }
}
