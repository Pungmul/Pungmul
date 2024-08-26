package pungmul.pungmul.repository.chat.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.chat.ChatMessage;

import java.util.List;

@Mapper
public interface ChatMapper {
    Long save(ChatMessage chatMessage);

    ChatMessage findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findBySenderOrRecipient(String userId);

    ChatMessage selectChatMessageById(Long saveId);
}
