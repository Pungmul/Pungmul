package pungmul.pungmul.service.message.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationTemplateFactory {
    public static String createBoardCommentMessage(String postTitle, String commentText) {
        return String.format("'%s' 게시글에 댓글이 달렸습니다: %s", postTitle, commentText);
    }

    public static String createFriendRequestMessage(String friendName) {
        return String.format("%s님이 친구 요청을 보냈습니다.", friendName);
    }

    public static String createMeetingInvitationMessage(String meetingTitle) {
        return String.format("'%s' 모임에 초대되었습니다.", meetingTitle);
    }
}
