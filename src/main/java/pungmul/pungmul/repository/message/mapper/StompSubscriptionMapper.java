package pungmul.pungmul.repository.message.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StompSubscriptionMapper {
    void insertSubscription(String sessionId, Long userId, String destination);

    List<Long> findUsersByDestination(String destination);

    void deleteBySessionId(String sessionId) ;

    void deleteByUserId(Long userId);
}
