package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.MessageType;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDTO {
    private String receiverUsername;
    private String content;
    private MessageType messageType;
    private String imageUrl;
}
