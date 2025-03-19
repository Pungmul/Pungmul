package pungmul.pungmul.repository.chat.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatRoomMemberMapper {
    Long getOpponentUserId(String chatRoomUUID, Long userId);

    List<Long> getOpponentMultiMemberNameList(String chatRoomUUID, Long userId);

    List<Long> findAllMembersByChatRoomId(String chatRoomUUID);
}
