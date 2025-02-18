package pungmul.pungmul.repository.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;
import pungmul.pungmul.repository.message.mapper.StompMessageLogMapper;
import pungmul.pungmul.repository.message.mapper.StompMessageReadStatusMapper;
import pungmul.pungmul.repository.message.repository.StompMessageReadStatusRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisStompMessageReadStatusRepository implements StompMessageReadStatusRepository {
    private final StompMessageReadStatusMapper stompMessageReadStatusMapper;

    @Override
    public void insertReadStatus(Long id, Long recipientUserId) {
        stompMessageReadStatusMapper.insertReadStatus(id, recipientUserId);
    }

    @Override
    public boolean isMessageRead(Long messageId, Long receiverId) {
        return stompMessageReadStatusMapper.isMessageRead(messageId, receiverId);
    }

    @Override
    public void markMessageAsRead(Long messageId, Long receiverId) {
        stompMessageReadStatusMapper.markMessageAsRead(messageId, receiverId);
    }

//    @Override
//    public List<StompMessageReadStatus> findUnreadMessages(Long receiverId) {
//        return stompMessageReadStatusMapper.findUnreadMessages(receiverId);
//    }
}
