package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.domain.message.NotificationContent;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.message.repository.FCMRepository;
import pungmul.pungmul.service.message.FCMService;
import pungmul.pungmul.service.message.template.LightningMeetingNotificationTemplateFactory;


import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class LightningMeetingNotificationTrigger {
    private final FCMService fcmService;
    private final FCMRepository fcmRepository;
    private final LightningMeetingRepository lightningMeetingRepository;
    private final UserRepository userRepository;

    public void triggerAddParticipant(Long meetingId, UserDetailsImpl userDetails) {

        Long participantId = userRepository.getUserIdByAccountId(userDetails.getAccountId());
        try {
            // 1. 모임 정보 조회
            LightningMeeting meeting = lightningMeetingRepository.getMeetingById(meetingId)
                    .orElseThrow(() -> new NoSuchElementException("해당 모임을 찾을 수 없습니다."));

            Long organizerId = meeting.getOrganizerId(); // 모임장 ID
            String meetingTitle = meeting.getMeetingName();

            // 2. 참가자 정보 조회
            String participantName = userRepository.getUserByUserId(participantId)
                    .map(User::getName)
                    .orElse("알 수 없는 사용자");

            // 3. 모임장 FCM 토큰 가져오기
            List<String> tokens = fcmRepository.findTokensByUserId(organizerId).stream()
                    .filter(FCMToken::isValid)
                    .map(FCMToken::getToken)
                    .toList();

            if (tokens.isEmpty()) {
                log.warn("모임장에게 보낼 유효한 FCM 토큰이 없습니다. OrganizerId={}", organizerId);
                return;
            }

            // 4. FCM 알림 메시지 생성
            NotificationContent notificationContent = LightningMeetingNotificationTemplateFactory
                    .createAddParticipantNotification(meetingTitle, participantName);

            // 5. FCM 알림 메시지 전송
            for (String token : tokens) {
                fcmService.sendNotification(token, notificationContent);
            }

            log.info("번개 모임 참가 요청 알림 전송 완료: MeetingId={}, OrganizerId={}, ParticipantId={}",
                    meetingId, organizerId, participantId);

        } catch (IOException e) {
            log.error("번개 모임 참가 요청 알림 전송 실패: MeetingId={}, Error={}", meetingId, e.getMessage());
        }
    }
}
