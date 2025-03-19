package pungmul.pungmul.repository.chat.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.repository.chat.mapper.ChatRoomMemberMapper;
import pungmul.pungmul.repository.chat.repository.ChatRoomMemberRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisChatRoomMemberRepository implements ChatRoomMemberRepository {
    private final ChatRoomMemberMapper chatRoomMemberMapper;
    @Override
    public Long getOpponentUserId(String chatRoomUUID, Long userId) {
        return chatRoomMemberMapper.getOpponentUserId(chatRoomUUID, userId);
    }

    @Override
    public List<Long> getOpponentMultiMemberNameList(String chatRoomUUID, Long userId) {
        return chatRoomMemberMapper.getOpponentMultiMemberNameList(chatRoomUUID, userId);
    }

    @Override
    public List<Long> findAllMembersByChatRoomId(String chatRoomUUID) {
        return chatRoomMemberMapper.findAllMembersByChatRoomId(chatRoomUUID);
    }
}
