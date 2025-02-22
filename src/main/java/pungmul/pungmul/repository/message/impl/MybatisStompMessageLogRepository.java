package pungmul.pungmul.repository.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessageLog;
import pungmul.pungmul.repository.message.mapper.StompMessageLogMapper;
import pungmul.pungmul.repository.message.repository.StompMessageLogRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisStompMessageLogRepository implements StompMessageLogRepository {
    private final StompMessageLogMapper stompMessageLogMapper;

    @Override
    public void insertStompMessageLog(StompMessageLog stompMessageLog) {
        stompMessageLogMapper.insertStompMessageLog(stompMessageLog);
    }

    @Override
    public List<StompMessageLog> findByDomainAndBusinessIdentifierAndIdentifier(String domain, String businessIdentifier, String identifier) {
        return stompMessageLogMapper.findByDomainAndBusinessIdentifierAndIdentifier(domain, businessIdentifier, identifier);
    }


    @Override
    public List<StompMessageLog> findByDomainAndBusinessIdentifier(MessageDomainType domainType, String businessIdentifier) {
        return stompMessageLogMapper.findByDomainAndBusinessIdentifier(domainType, businessIdentifier);
    }

    @Override
    public List<StompMessageLog> findByDomainType(MessageDomainType domainType) {
        return stompMessageLogMapper.findByDomainType(domainType);
    }

    @Override
    public List<StompMessageLog> findAll() {
        return stompMessageLogMapper.findAll();
    }

    @Override
    public void deleteById(Long logId) {
        stompMessageLogMapper.deleteById(logId);
    }

    @Override
    public List<StompMessageLog> findUnreadMessages(Long receiverId) {
        return stompMessageLogMapper.findUnreadMessages(receiverId);
    }
}
