package pungmul.pungmul.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMTokenRequestDTO {
    private String fcmToken;
    private String deviceInfo;
}
