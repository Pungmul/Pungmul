package pungmul.pungmul.service.message.template;

import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.message.NotificationContent;

public class LightningMeetingNotificationTemplateFactory {
    public static NotificationContent createAddParticipantNotification(String meetingTitle, String participantName) {
        return NotificationContent.builder()
                .title(meetingTitle)
                .body(participantName + "님이 참가 요청")
                .build();
    }

    public static NotificationContent createMeetingSuccessNotification(LightningMeeting meeting) {
        return NotificationContent.builder()
                .title("모임 성사 알림")
                .body(meeting.getMeetingName() + " 번개 모임이 " + meeting.getStartTime() + "부터 시작됩니다.")
                .build();
    }
    public static NotificationContent createMeetingFailedNotification(LightningMeeting meeting) {
        return NotificationContent.builder()
                .title("모임 취소 알림")
                .body(meeting.getMeetingName() + " 번개 모임이 취소되었습니다.")
                .build();
    }

    public static NotificationContent remindMeetingNotification(LightningMeeting meeting) {
        return NotificationContent.builder()
                .title(meeting.getMeetingName() + " 30분 전! ")
                .body("잊지 않으셨죠?")
                .build();
    }
}
