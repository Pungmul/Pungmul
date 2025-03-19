package pungmul.pungmul.repository.chat.repository;

import java.util.List;

public interface ChatRoomMemberRepository {
    Long getOpponentUserId(String chatRoomUUID, Long userId);

    List<Long> getOpponentMultiMemberNameList(String chatRoomUUID, Long userId);

    List<Long> findAllMembersByChatRoomId(String chatRoomUUID);
}
