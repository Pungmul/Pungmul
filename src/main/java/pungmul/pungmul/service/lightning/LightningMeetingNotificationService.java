//package pungmul.pungmul.service.lightning;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LightningMeetingNotificationService {
//    private final FCMService fcmService;
//    private final FCMRepository fcmRepository;
//
//    public void sendMeetingSuccessNotification(LightningMeeting meeting) {
//        List<String> tokens = getParticipantTokens(meeting.getId());
//        NotificationContent content = LightningMeetingNotificationTemplateFactory.createMeetingSuccessNotification(meeting);
//
//        for (String token : tokens) {
//            fcmService.sendNotification(token, content);
//        }
//    }
//
//    public void sendMeetingCancelNotification(LightningMeeting meeting) {
//        List<String> tokens = getParticipantTokens(meeting.getId());
//        NotificationContent content = LightningMeetingNotificationTemplateFactory.createMeetingFailedNotification(meeting);
//
//        for (String token : tokens) {
//            fcmService.sendNotification(token, content);
//        }
//    }
//
//    private List<String> getParticipantTokens(Long meetingId) {
//        return fcmRepository.findTokensByMeetingId(meetingId)
//                .stream()
//                .filter(FCMToken::isValid)
//                .map(FCMToken::getToken)
//                .toList();
//    }
//
//    public void askOrganizerForMeetingApproval(LightningMeeting meeting) {
//        // Organizer 승인 요청 로직 (WebSocket 메시지 등)
//    }
//}
