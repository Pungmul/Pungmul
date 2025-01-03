package pungmul.pungmul.repository.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.core.exception.custom.chat.NoSuchChatroomException;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;
import pungmul.pungmul.dto.chat.FindChatRoomByUserIdDTO;
import pungmul.pungmul.dto.chat.UpdateLastMessageDTO;
import pungmul.pungmul.repository.chat.mapper.ChatRoomMapper;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisChatRoomRepository implements ChatRoomRepository {
    private final ChatRoomMapper chatRoomMapper;
    @Override
    public String createChatRoom(ChatRoom chatRoom) {
        chatRoomMapper.createChatRoom(chatRoom);
        return chatRoom.getRoomUUID();
    }

    @Override
    public void addChatRoomMembers(String chatRoomId, List<Long> memberIds) {
        log.info("call members");
        chatRoomMapper.addChatRoomMembers(chatRoomId, memberIds);
    }

    @Override
    public Optional<ChatRoom> findChatRoomByUsers(String senderUsername, String receiverUsername) {
        return chatRoomMapper.findChatRoomByUsers(senderUsername, receiverUsername);
    }

    @Override
    public List<ChatRoom> findChatRoomsByUserId(Long id, String username, Integer size, Integer offset) {
        //        log.info("chatroom info : {}", chatRooms.get(0).getRoomName());

        return chatRoomMapper.findChatRoomsByUserId(
                FindChatRoomByUserIdDTO.builder()
                        .userId(id)
                        .username(username)
                        .limit(size)
                        .offset(offset)
                        .build());
    }

    @Override
    public void updateLastMessage(String chatRoomUUID, Long messageId) {
        log.info("messageId : {}", messageId);
        chatRoomMapper.updateLastMessage(UpdateLastMessageDTO.builder()
                .messageId(messageId)
                .chatRoomUUID(chatRoomUUID)
                .build());
    }

    @Override
    public ChatRoom findChatRoomByUUID(String chatRoomUUID) {
        return chatRoomMapper.findChatRoomByUUID(chatRoomUUID);
    }

    @Override
    public List<Long> findChatRoomMemberList(String chatRoomUUID) {
        return chatRoomMapper.findChatRoomMemberList(chatRoomUUID);
    }
}
