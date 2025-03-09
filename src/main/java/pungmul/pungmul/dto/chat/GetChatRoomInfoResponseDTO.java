package pungmul.pungmul.dto.chat;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.member.SimpleUserDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomInfoResponseDTO {
    private ChatRoomDTO chatRoomInfo;
    private List<SimpleUserDTO> userInfoList;
    private PageInfo<ChatMessage> messageList;
}
