package pungmul.pungmul.repository.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.chat.mapper.ChatRoomMapper;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisChatRoomRepository implements ChatRoomRepository {
    private final ChatRoomMapper chatRoomMapper;
    @Override
    public String createPersonalChatRoom(ChatRoom chatRoom) {
        chatRoomMapper.createPersonalChatRoom(chatRoom);
        return chatRoom.getRoomUUID();
    }

    @Override
    public void addChatRoomMembers(String chatRoomId, List<Long> memberIds) {
        log.info("call members");
        chatRoomMapper.addChatRoomMembers(chatRoomId, memberIds);
    }
}
