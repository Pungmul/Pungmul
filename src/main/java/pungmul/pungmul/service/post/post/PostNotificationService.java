package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostNotificationService {
    private final PostNotificationTrigger postNotificationTrigger;

    public void triggerLikeNotification(Long postId, Long userId) {
        postNotificationTrigger.triggerLikeNotification(postId, userId);
    }
}
