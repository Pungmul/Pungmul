package pungmul.pungmul.repository.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.message.FCMMessageLog;

import java.util.List;

@Mapper
public interface FCMLogMapper {
    void insertFCMMessageLog(FCMMessageLog fcmMessageLog);

    List<FCMMessageLog> getFCMLogsByReceiverId(Long receiverId);

    List<FCMMessageLog> getAllFCMLogs();

    void deleteFCMLogById(Long id);
}
