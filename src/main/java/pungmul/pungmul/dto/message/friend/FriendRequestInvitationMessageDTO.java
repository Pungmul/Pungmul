package pungmul.pungmul.dto.message.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.message.StompMessageDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestInvitationMessageDTO {
    private Long friendRequestId;
//    private Long senderId;         // 요청을 보낸 사용자 ID
    private String senderUsername;     // 요청을 보낸 사용자 userName
//    private String senderName;
    private String receiverUsername;
    private String content;        // 알림 메시지 내용
//    private LocalDateTime sentAt;  // 알림 생성 시간
}
