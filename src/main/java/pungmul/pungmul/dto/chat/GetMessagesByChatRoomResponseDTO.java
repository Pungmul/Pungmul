package pungmul.pungmul.dto.chat;

import com.github.pagehelper.PageInfo;
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
    PageInfo<ChatMessage> chatMessageList;
}
