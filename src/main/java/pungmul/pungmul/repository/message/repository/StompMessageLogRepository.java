package pungmul.pungmul.repository.message.repository;

import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessageLog;

import java.util.List;

public interface StompMessageLogRepository {
     void insertStompMessageLog(StompMessageLog stompMessageLog);

     List<StompMessageLog> findByDomainAndBusinessIdentifierAndIdentifier(String domain, String businessIdentifier, String identifier);

     List<StompMessageLog> findByDomainAndBusinessIdentifier(MessageDomainType domainType, String businessIdentifier);

     List<StompMessageLog> findByDomainType(MessageDomainType domainType);

     List<StompMessageLog> findAll();

     void deleteById(Long logId);

     List<StompMessageLog> findUnreadMessages(Long receiverId);
}
