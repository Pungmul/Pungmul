package pungmul.pungmul.repository.chat.repository;

import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;

import java.util.List;

public interface ChatRepository {
    ChatMessage save(ChatMessage chatMessage);

    ChatMessage findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findBySenderOrRecipient(String userId);

    String getLastMessageByChatRoomUUID(String chatRoomUUID);
}
