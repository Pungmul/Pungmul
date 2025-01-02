package pungmul.pungmul.repository.chat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;
import pungmul.pungmul.dto.chat.FindChatRoomByUserIdDTO;
import pungmul.pungmul.dto.chat.UpdateLastMessageDTO;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatRoomMapper {
    void createChatRoom(ChatRoom chatRoom);

    void addChatRoomMembers(@Param("chatRoomId") String chatRoomId, @Param("memberIds") List<Long> memberIds);

    Optional<ChatRoom> findChatRoomByUsers(@Param("senderUsername") String senderUsername, @Param("receiverUsername") String receiverUsername);

    List<ChatRoom> findChatRoomsByUserId(FindChatRoomByUserIdDTO findChatRoomByUserIdDTO);

    void updateLastMessage(UpdateLastMessageDTO updateLastMessageDTO);

    ChatRoom findChatRoomByUUID(String chatRoomUUID);

    List<Long> findChatRoomMemberList(String chatRoomUUID);
}
