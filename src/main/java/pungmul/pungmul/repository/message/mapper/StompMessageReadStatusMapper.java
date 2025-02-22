package pungmul.pungmul.repository.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;

import java.util.List;

@Mapper
public interface StompMessageReadStatusMapper {
    // 메시지 읽음 상태 삽입 (초기값: is_read = FALSE)
    void insertReadStatus(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId);

    // 특정 메시지를 사용자가 읽었는지 여부 확인
    boolean isMessageRead(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId);

    // 메시지를 읽었을 때 읽음 상태 갱신
    void markMessageAsRead(@Param("messageIdList") List<Long> messageIdList, @Param("receiverId") Long receiverId);

    // 특정 사용자의 안 읽은 메시지 조회
//    List<StompMessageReadStatus> findUnreadMessages(@Param("receiverId") Long receiverId);
}
