package pungmul.pungmul.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private Long id;                     // 채팅방 고유 ID
    private String roomUUID;             // 채팅방 UUID
    private Long createdBy;              // 생성한 사용자 ID
    private boolean isGroup;             // 단체 채팅 여부
    private String roomName;             // 단체 채팅방 이름 (단체 채팅일 경우)
    private String profileImageUrl;      // 단체 채팅방 이미지 (단체 채팅일 경우)
    private Long lastMessageId;          // 마지막 메시지 ID
    private LocalDateTime lastMessageTime; // 마지막 메시지 시간
    private LocalDateTime createdAt;     // 방 생성 시간
    private Integer unreadCount;

    // 1:1 채팅에서만 사용
    private Long senderId;               // 메시지 보낸 사용자 ID
    private Long receiverId;             // 메시지 받은 사용자 ID

    // 단체 채팅에서 사용
    @Builder.Default
    private List<Long> chatRoomMemberIds = new ArrayList<>();
}
