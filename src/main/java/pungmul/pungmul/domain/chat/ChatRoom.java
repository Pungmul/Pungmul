package pungmul.pungmul.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ChatRoom {
    private Long id;
    private String roomUUID;
    private String createdBy;
    private Long lastMessageId;
    private LocalDateTime lastMessageTime;
    private List<Long> chatRoomMemberIds;
    private LocalDateTime createdAt;
}
