package pungmul.pungmul.repository.message.repository;

import pungmul.pungmul.domain.member.user.User;

import java.util.List;

public interface StompSubscriptionRepository {
    void insertSubscription(String sessionId, Long userId, String destination);

    List<Long> findUsersByDestination(String destination);

    void deleteBySessionId(String sessionId);

    void deleteByUserId(Long userId);

    void deleteSubscription(String sessionId, Long userId, String destination);

    Long findUserIdByUsername(String username);

}