package pungmul.pungmul.repository.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;
import pungmul.pungmul.repository.chat.mapper.ChatMapper;
import pungmul.pungmul.repository.chat.repository.ChatRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisChatRepository implements ChatRepository {
    private final ChatMapper chatMapper;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        chatMapper.save(chatMessage);
//        return chatMapper.selectChatMessageById(saveId);
        return chatMessage;
    }

    @Override
    public ChatMessage findByChatRoomId(Long chatRoomId) {
        return chatMapper.findByChatRoomId(chatRoomId);
    }

    @Override
    public List<ChatMessage> findBySenderOrRecipient(String userId) {
        return chatMapper.findBySenderOrRecipient(userId);
    }

    @Override
    public String getLastMessageByChatRoomUUID(String chatRoomUUID) {
        return chatMapper.getLastMessageByChatRoomUUID(chatRoomUUID);
    }
}
