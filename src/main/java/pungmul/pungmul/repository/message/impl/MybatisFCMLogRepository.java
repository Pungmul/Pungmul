package pungmul.pungmul.repository.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.message.FCMMessageLog;
import pungmul.pungmul.repository.message.mapper.FCMLogMapper;
import pungmul.pungmul.repository.message.repository.FCMLogRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisFCMLogRepository implements FCMLogRepository {
    private final FCMLogMapper fcmLogMapper;

    @Override
    public void insertFCMMessageLog(FCMMessageLog fcmMessageLog) {
        fcmLogMapper.insertFCMMessageLog(fcmMessageLog);
    }

    @Override
    public List<FCMMessageLog> getFCMLogsByReceiverId(Long receiverId) {
        return fcmLogMapper.getFCMLogsByReceiverId(receiverId);
    }

    @Override
    public List<FCMMessageLog> getAllFCMLogs() {
        return fcmLogMapper.getAllFCMLogs();
    }

    @Override
    public void deleteFCMLogById(Long id) {
        fcmLogMapper.deleteFCMLogById(id);
    }
}
