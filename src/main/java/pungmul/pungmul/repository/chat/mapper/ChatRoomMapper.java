package pungmul.pungmul.repository.chat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.chat.ChatRoom;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatRoomMapper {
    void createPersonalChatRoom(ChatRoom chatRoom);

    void addChatRoomMembers(@Param("chatRoomId") String chatRoomId, @Param("memberIds") List<Long> memberIds);

    Optional<ChatRoom> findChatRoomByUsers(@Param("senderUsername") String senderUsername, @Param("receiverUsername") String receiverUsername);
}
