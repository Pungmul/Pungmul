package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.domain.message.NotificationContent;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.repository.post.repository.CommentRepository;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.service.message.FCMService;
import pungmul.pungmul.repository.message.repository.FCMRepository;
import pungmul.pungmul.service.message.template.PostNotificationTemplateFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostNotificationTrigger {
    private final FCMService fcmService;
    private final FCMRepository fcmRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;

    // 댓글 알림
    public void triggerCommentNotification(Long postId, Long commenterId, String commentContent) {
        try {
            Content content = contentRepository.getContentByPostId(postId).orElseThrow(NoSuchElementException::new);
            Long writerId = content.getWriterId();


            if (!writerId.equals(commenterId)) {
                List<String> tokens = fcmRepository.findTokensByUserId(writerId).stream()
                        .filter(FCMToken::isValid)
                        .map(FCMToken::getToken)
                        .toList();

                NotificationContent commentNotification = PostNotificationTemplateFactory.createCommentNotification(content.getTitle(), commentContent);

                for (String token : tokens) {
                    fcmService.sendNotification(token, commentNotification);
                }
                log.info("댓글 알림 전송 완료: PostId={}, AuthorId={}", postId, writerId);
            }
        } catch (IOException e) {
            log.error("댓글 알림 전송 실패: PostId={}, Error={}", postId, e.getMessage());
        }
    }

    // 좋아요 알림
    public void triggerLikeNotification(Long postId, Long likerId) {
        try {
//            Post post = postService.getPostEntityById(postId);
            Content content = contentRepository.getContentByPostId(postId).orElseThrow(NoSuchElementException::new);
            Long postAuthorId = content.getWriterId();

            if (!postAuthorId.equals(likerId)) {
                List<String> tokens = fcmRepository.findTokensByUserId(postAuthorId).stream()
                        .filter(FCMToken::isValid)
                        .map(FCMToken::getToken)
                        .toList();

                NotificationContent postLikeNotification = PostNotificationTemplateFactory.createPostLikeNotification(content.getTitle());

                for (String token : tokens) {
                    fcmService.sendNotification(token, postLikeNotification);
                }
                log.info("좋아요 알림 전송 완료: PostId={}, AuthorId={}", postId, postAuthorId);
            }
        } catch (IOException e) {
            log.error("좋아요 알림 전송 실패: PostId={}, Error={}", postId, e.getMessage());
        }
    }

    // 답글 알림
    public void triggerReplyNotification(Long commentId, Long replierId, String replyContent) {
        try {
            Long postId = commentRepository.getCommentByCommentId(commentId).getPostId();
            String postTitle = contentRepository.getContentByPostId(postId).map(Content::getTitle).orElseThrow(NoSuchElementException::new);
            Comment parentComment = commentRepository.getCommentByCommentId(commentId);
            Long commentAuthorId = parentComment.getUserId();

            if (!commentAuthorId.equals(replierId)) {
                List<String> tokens = fcmRepository.findTokensByUserId(commentAuthorId).stream()
                        .filter(FCMToken::isValid)
                        .map(FCMToken::getToken)
                        .toList();

                NotificationContent replyNotification = PostNotificationTemplateFactory.createReplyNotification(postTitle, replyContent);

                for (String token : tokens) {
                    fcmService.sendNotification(token, replyNotification);
                }
                log.info("답글 알림 전송 완료: CommentId={}, AuthorId={}", commentId, commentAuthorId);
            }
        } catch (IOException e) {
            log.error("답글 알림 전송 실패: CommentId={}, Error={}", commentId, e.getMessage());
        }
    }
}
