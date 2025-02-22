package pungmul.pungmul.repository.message.repository;

import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.message.StompMessageLog;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;

import java.util.List;

public interface StompMessageReadStatusRepository {
    void insertReadStatus(Long id, Long recipientUserId);

    // 특정 메시지를 사용자가 읽었는지 여부 확인
    boolean isMessageRead(Long messageId, Long receiverId);

    // 메시지를 읽었을 때 읽음 상태 갱신
    void markMessageAsRead(List<Long> messageIdList, Long receiverId);

    // 특정 사용자의 안 읽은 메시지 조회
//    List<StompMessageLog> findUnreadMessages(Long receiverId);
}
