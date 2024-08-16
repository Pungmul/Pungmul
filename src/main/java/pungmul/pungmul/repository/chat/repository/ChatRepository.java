package pungmul.pungmul.repository.chat.repository;

import pungmul.pungmul.dto.chat.ChatMessage;

import java.util.List;

public interface ChatRepository {
    void save(ChatMessage chatMessage);

    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findBySenderOrRecipient(String userId);
}
