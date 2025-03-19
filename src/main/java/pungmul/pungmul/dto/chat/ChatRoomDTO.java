package pungmul.pungmul.dto.chat;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private String chatRoomUUID;          // 채팅방 UUID
    private boolean isGroup;            // 단체 채팅 여부

    private String roomName;
    private String profileImageUrl;     // 단체 채팅방 이미지 (단체 채팅일 경우)
}
