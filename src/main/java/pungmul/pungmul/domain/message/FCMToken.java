package pungmul.pungmul.domain.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMToken {
    private Long id;
    private Long userId;         // 사용자 ID
    private String token;        // FCM 토큰
    private String deviceInfo;   // 디바이스 정보 (Android, iOS, Web)
    private boolean isValid;     // 토큰 유효성 여부
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 업데이트 시간
}
