package pungmul.pungmul.repository.chat.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.dto.chat.ChatMessage;

import java.util.List;

@Mapper
public interface ChatMapper {
    void save(ChatMessage chatMessage);

    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findBySenderOrRecipient(String userId);
}
