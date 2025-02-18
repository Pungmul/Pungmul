package pungmul.pungmul.repository.message.repository;

import java.util.List;

public interface StompSubscriptionRepository {
    void insertSubscription(String sessionId, Long userId, String destination);

    List<Long> findUsersByDestination(String destination);

    void deleteBySessionId(String sessionId) ;

     void deleteByUserId(Long userId);

}