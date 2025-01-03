package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.chat.ChatMessage;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMessagesByChatRoomResponseDTO {
    List<ChatMessage> chatMessageList;
}
