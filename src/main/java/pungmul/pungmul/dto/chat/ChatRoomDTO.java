package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private String chatRoomUUID;          // 채팅방 UUID
    private boolean isGroup;            // 단체 채팅 여부
    private LocalDateTime lastMessageTime; // 마지막 메시지 시간
    private String lastMessageContent;  // 마지막 메시지 내용
    private Integer unreadCount;        // 읽지 않은 메시지 개수

    private Long senderId;              // 메시지 보낸 사용자 ID (1:1 채팅일 경우)
    private String senderName;
    private Long receiverId;            // 메시지 받은 사용자 ID (1:1 채팅일 경우)
    private String receiverName;

    private List<Long> chatRoomMemberIds; // 단체 채팅방 멤버 ID 리스트
    private List<String> chatRoomMemberNames;

    private String roomName;            // 단체 채팅방 이름 (단체 채팅일 경우)
    private String profileImageUrl;     // 단체 채팅방 이미지 (단체 채팅일 경우)
}
