package pungmul.pungmul.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMMessageLog {
    private Long id;
    private Long userId;
    private String token;
    private String title;
    private String body;
    private LocalDateTime sentAt;
    private String status;
    private String response; // Firebase 응답 저장
    private MessageDomainType domainType;
}
