package pungmul.pungmul.repository.chat.repository;

import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;

import java.util.List;

public interface ChatRepository {
    ChatMessage save(ChatMessage chatMessage);

    ChatMessage findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findBySenderOrRecipient(String userId);

    ChatMessage getLastMessageByChatRoomUUID(String chatRoomUUID);

    List<ChatMessage> getMessagesByChatRoom(@Param("chatRoomUUID") String chatRoomUUID,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset);
    //    ChatMessage getLastMessageByRoomUUID(String chatRoomUUID);
}
