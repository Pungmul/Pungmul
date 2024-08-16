package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String senderUsername;  // userId 대신 username으로 변경
    private String recipientUsername;  // userId 대신 username으로 변경
    private String content;
    private MessageType messageType;
    private String imageUrl;
    private Long chatRoomId;
    private LocalDateTime time;

    public enum MessageType {
        CHAT, JOIN, LEAVE, IMAGE
    }
}
