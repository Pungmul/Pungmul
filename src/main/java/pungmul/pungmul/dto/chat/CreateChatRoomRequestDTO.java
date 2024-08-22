package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomRequestDTO {
    private String senderName;
    private String receiverName;
}
