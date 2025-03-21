package pungmul.pungmul.repository.chat.repository;

import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    String createChatRoom(ChatRoom chatRoom);

    void addChatRoomMembers(String chatRoomId, List<Long> memberIds);

    Optional<ChatRoom> findChatRoomByUsers(String senderUsername, String receiverUsername);

    List<ChatRoom> findChatRoomsByUserId(Long id, String username, Integer limit, Integer offset);

    void updateLastMessage(String chatRoomUUID, Long messageId);

    ChatRoom findChatRoomByUUID(String chatRoomUUID);

    List<Long> findChatRoomMemberList(String chatRoomUUID);

}
