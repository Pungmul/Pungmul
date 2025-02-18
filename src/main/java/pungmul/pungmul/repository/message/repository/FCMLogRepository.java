package pungmul.pungmul.repository.message.repository;

import pungmul.pungmul.domain.message.FCMMessageLog;

import java.util.List;

public interface FCMLogRepository {
    void insertFCMMessageLog(FCMMessageLog fcmMessageLog);

    List<FCMMessageLog> getFCMLogsByReceiverId(Long receiverId);

    List<FCMMessageLog> getAllFCMLogs();

    void deleteFCMLogById(Long id);
}
