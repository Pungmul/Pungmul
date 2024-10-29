package pungmul.pungmul.dto.message.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.message.DomainMessage;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestInvitationMessageDTO implements DomainMessage {
    private Long senderId;         // 요청을 보낸 사용자 ID
    private String senderName;     // 요청을 보낸 사용자 이름
    private String content;        // 알림 메시지 내용
    private LocalDateTime sentAt;  // 알림 생성 시간

    @Override
    public String getMessageContent() {
        return content;
    }
}
