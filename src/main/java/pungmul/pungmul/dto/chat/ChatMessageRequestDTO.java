package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.chat.ChatType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDTO {
//    private String receiverUsername;
    private String content;
    private ChatType chatType;
    private String chatRoomUUID;
    private String imageUrl;
}
