package pungmul.pungmul.repository.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessageLog;

import java.util.List;

@Mapper
public interface StompMessageLogMapper {
    void insertStompMessageLog(StompMessageLog stompMessageLog) ;

    List<StompMessageLog> findByDomainAndBusinessIdentifierAndIdentifier(String domain, String businessIdentifier, String identifier) ;

    List<StompMessageLog> findByDomainAndBusinessIdentifier(MessageDomainType domainType, String businessIdentifier) ;

    List<StompMessageLog> findByDomainType(MessageDomainType domainType) ;

    List<StompMessageLog> findAll() ;

    void deleteById(Long logId);

    List<StompMessageLog> findUnreadMessages(Long receiverId);

     void insertReadStatus(Long id, Long recipientUserId, boolean isRead);
}
