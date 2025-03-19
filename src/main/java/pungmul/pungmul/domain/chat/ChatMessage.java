package pungmul.pungmul.domain.chat;

import lombok.*;
import pungmul.pungmul.dto.Mappable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// DTO랑 DOMAIN 분리해야함


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Mappable {
    private Long id;
    private String senderUsername;  // userId 대신 username으로 변경
    private String content;
    private ChatType chatType;
    private String imageUrl;
    private String chatRoomUUID;
    private LocalDateTime createdAt;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("senderUsername", senderUsername);
        map.put("content", content);
        map.put("chatType", chatType);
        map.put("chatRoomUUID", chatRoomUUID);
        map.put("createdAt", createdAt);
        if (imageUrl != null) {
            map.put("imageUrl", imageUrl); // 이미지 URL이 있을 경우 포함
        }
        return map;
    }
}
