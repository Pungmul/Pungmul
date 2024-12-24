package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.repository.post.repository.PostLimitRepository;

@Service
@RequiredArgsConstructor
public class PostLimitService {
    private final PostLimitRepository postLimitRepository;

    @Scheduled(cron = "0 0 5 * * ?") // 매일 새벽 5시에 실행
    @Transactional
    public void resetPostLimits() {
        postLimitRepository.resetAllPostCount();
    }

    public void incrementPostCount(Long userId) {
        postLimitRepository.incrementPostCount(userId);
    }
}
