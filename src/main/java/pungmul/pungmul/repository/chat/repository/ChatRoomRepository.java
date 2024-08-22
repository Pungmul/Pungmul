package pungmul.pungmul.repository.chat.repository;

import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.domain.member.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    String createPersonalChatRoom(ChatRoom chatRoom);

    void addChatRoomMembers(String chatRoomId, List<Long> memberIds);


}
