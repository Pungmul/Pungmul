package pungmul.pungmul.repository.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.repository.message.mapper.StompSubscriptionMapper;
import pungmul.pungmul.repository.message.repository.StompSubscriptionRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisStompSubscriptionRepository implements StompSubscriptionRepository {
    private final StompSubscriptionMapper stompSubscriptionMapper;

    /**
     * STOMP 구독 정보 저장
     */
    public void insertSubscription(String sessionId, Long userId, String destination) {
        stompSubscriptionMapper.insertSubscription(sessionId, userId, destination);
    }

    /**
     * 특정 STOMP 경로를 구독 중인 사용자 조회
     */
    public List<Long> findUsersByDestination(String destination) {
        return stompSubscriptionMapper.findUsersByDestination(destination);
    }

    /**
     * 세션 종료 시 해당 세션의 모든 구독 정보 삭제
     */
    public void deleteBySessionId(String sessionId) {
        stompSubscriptionMapper.deleteBySessionId(sessionId);
    }

    /**
     * 사용자가 로그아웃하면 모든 구독 정보 삭제
     */
    public void deleteByUserId(Long userId) {
        stompSubscriptionMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteSubscription(String sessionId, Long userId, String destination) {

    }

    @Override
    public Long findUserIdByUsername(String username) {
        return stompSubscriptionMapper.findUserIdByUsername(username);
    }
}
