package pungmul.pungmul.service.message.template;

import pungmul.pungmul.domain.message.NotificationContent;

public class LightningMeetingNotificationTemplateFactory {
    public static NotificationContent createAddParticipantNotification(String meetingTitle, String participantName) {
        return NotificationContent.builder()
                .title(meetingTitle)
                .body(participantName + "님이 참가 요청")
                .build();
    }
}
