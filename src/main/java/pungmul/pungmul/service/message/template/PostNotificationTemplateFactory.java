package pungmul.pungmul.service.message.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.message.NotificationContent;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostNotificationTemplateFactory {
    // 댓글 알림
    public static NotificationContent createCommentNotification(String postTitle, String commentText) {
        return NotificationContent.builder()
                .title(postTitle)
                .body(commentText)
                .build();
    }

    public static NotificationContent createReplyNotification(String postTitle, String commentText) {
        return NotificationContent.builder()
                .title(postTitle)
                .body(commentText)
                .build();
    }

    // 좋아요 알림
    public static NotificationContent createPostLikeNotification(String postTitle) {
        return NotificationContent.builder()
                .title(postTitle)
                .body("")
                .build();
    }

    public static String createCommentLikeNotification(String commentText, String likerName) {
        return String.format("'%s' 댓글에 %s님이 좋아요를 눌렀습니다.", commentText, likerName);
    }

    // 게시글 관련
    public static String createHotPostNotification(String postTitle) {
        return String.format("'%s' 게시글이 인기글로 선정되었습니다!", postTitle);
    }

    public static String createPostRemovedNotification(String postTitle) {
        return String.format("'%s' 게시글이 신고로 인해 삭제되었습니다.", postTitle);
    }

    // 팔로우 관련
    public static String createFollowerPostNotification(String followerName, String postTitle) {
        return String.format("팔로우한 %s님이 새 게시글을 작성했습니다: '%s'", followerName, postTitle);
    }

    // 기타
    public static String createTrendingCommentsNotification(String postTitle) {
        return String.format("'%s' 게시글에 새로운 댓글이 많이 달렸습니다!", postTitle);
    }

    public static String createAdminNoticeNotification(String noticeTitle) {
        return String.format("관리자 공지: '%s'", noticeTitle);
    }
}
