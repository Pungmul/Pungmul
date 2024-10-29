package pungmul.pungmul.domain.chat;

import lombok.*;

import java.time.LocalDateTime;

// DTO랑 DOMAIN 분리해야함


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long id;
    private String senderUsername;  // userId 대신 username으로 변경
    private String receiverUsername;  // userId 대신 username으로 변경
    private String content;
    private ChatType chatType;
    private String imageUrl;
    private String chatRoomUUID;
    private LocalDateTime timestamp;
}
