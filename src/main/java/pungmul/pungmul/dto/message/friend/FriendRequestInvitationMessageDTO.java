package pungmul.pungmul.dto.message.friend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.Mappable;
import pungmul.pungmul.dto.message.StompMessageDTO;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestInvitationMessageDTO implements Mappable {
    private Long friendRequestId;
    private String senderUsername;     // 요청을 보낸 사용자 userName
    private String receiverUsername;
    private String content;        // 알림 메시지 내용

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("friendRequestId", friendRequestId);
        map.put("senderUsername", senderUsername);
        map.put("receiverUsername", receiverUsername);
        map.put("content", content);
        return map;
    }

    public static ChatMessage fromMap(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, ChatMessage.class);
    }
}
