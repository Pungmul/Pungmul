package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.lightning.*;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.NotificationContent;
import pungmul.pungmul.domain.message.domain.LightningMeetingBusinessIdentifier;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.repository.message.repository.FCMRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.message.FCMService;
import pungmul.pungmul.service.message.MessageService;
import pungmul.pungmul.service.message.template.LightningMeetingNotificationTemplateFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingStatusManager {

    private final LightningMeetingRepository lightningMeetingRepository;
    private final LightningMeetingParticipantRepository lightningMeetingParticipantRepository;
    private final FCMService fcmService;
    private final FCMRepository fcmRepository;
    private final LightningMeetingInstrumentAssignmentRepository lightningMeetingInstrumentAssignmentRepository;
    private final LightningMeetingEventListener eventListener;
    private final MessageService messageService;
    private final UserService userService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void processMeetingsAndCheckConditions() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        processExpiredMeetings(now);
        processSuccessfulMeetings(now);
        checkAndCancelUnsuccessfulMeetings(now);
        deactivateParticipantsAfterMeetingEnd(now);
    }

    /** 🔹 모집 기간이 만료된 모임 처리 */
    private void processExpiredMeetings(LocalDateTime now) throws IOException {
        List<LightningMeeting> expiredMeetings = lightningMeetingRepository.findAllByDeadlineAndStatus(now, LightningMeetingStatus.OPEN);
//        for (LightningMeeting meeting : expiredMeetings) {
//            boolean isMeetingSuccessful = checkMeetingSuccess(meeting);
//
//            if (isMeetingSuccessful) {
//                startLightningMeeting(meeting);
//                lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.SUCCESS);
//                sendMeetingSuccessNotification(meeting);
//            } else {
//                cancelLightningMeeting(meeting);
//                lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.CANCELLED);
//                sendMeetingCancelNotification(meeting);
//            }
//
//            lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
//        }
        for (LightningMeeting meeting : expiredMeetings) {
            boolean isMeetingSuccessful = checkMeetingSuccess(meeting);

            if (isMeetingSuccessful) {
                startLightningMeeting(meeting);
                lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.SUCCESS);
                sendMeetingSuccessNotification(meeting);
            } else {
                cancelLightningMeeting(meeting);
                lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.CANCELLED);
                sendMeetingCancelNotification(meeting);

                // ✅ 모임이 취소된 경우에만 INACTIVE 처리
                lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
            }
        }
    }

    /** 🔹 성사된 모임 처리 */
    private void processSuccessfulMeetings(LocalDateTime now) {
        List<LightningMeeting> successfulMeetings = lightningMeetingRepository.findSuccessfulMeetingsWithoutNotification(now);

        for (LightningMeeting meeting : successfulMeetings) {
            askOrganizerForMeetingApproval(meeting);
            lightningMeetingRepository.markNotificationAsSent(meeting.getId());
        }
    }

    /** 🔹 시작 시간이 지난 실패한 모임을 취소 */
    private void checkAndCancelUnsuccessfulMeetings(LocalDateTime now) {
        List<LightningMeeting> unsuccessfulMeetings = lightningMeetingRepository.findUnsuccessfulMeetingsPastStartTime(now);
//        if (!unsuccessfulMeetings.isEmpty()) {
//            lightningMeetingRepository.cancelMeetingsPastStartTime(now);
//            List<Long> meetingIds = unsuccessfulMeetings.stream().map(LightningMeeting::getId).toList();
//            lightningMeetingParticipantRepository.deactivateParticipantsByMeetingIds(meetingIds);
//
//            log.info("총 {}개의 모임이 start_time이 지났지만 SUCCESS 상태가 아니므로 취소됨", unsuccessfulMeetings.size());
//        }
        if (!unsuccessfulMeetings.isEmpty()) {
            lightningMeetingRepository.cancelMeetingsPastStartTime(now);
            List<Long> meetingIds = unsuccessfulMeetings.stream().map(LightningMeeting::getId).toList();

            // ✅ 실패한 모임의 참가자들을 INACTIVE로 처리
            lightningMeetingParticipantRepository.deactivateParticipantsByMeetingIds(meetingIds);

//            log.info("총 {}개의 모임이 start_time이 지났지만 SUCCESS 상태가 아니므로 취소됨", unsuccessfulMeetings.size());
        }
    }

    /** 🔹 종료된 모임의 참가자 상태를 INACTIVE로 변경 */
    private void deactivateParticipantsAfterMeetingEnd(LocalDateTime now) {
        List<LightningMeeting> endedMeetings = lightningMeetingRepository.findMeetingsPastEndTime(now);
//        for (LightningMeeting meeting : endedMeetings) {
//            lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
//            log.info("모임 종료 후 참여자 상태 변경 완료: MeetingId={}", meeting.getId());
//        }
        for (LightningMeeting meeting : endedMeetings) {
            if (meeting.getStatus() == LightningMeetingStatus.SUCCESS) {  // ✅ SUCCESS 상태만 처리
                lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
//                log.info("모임 종료 후 참여자 상태 변경 완료: MeetingId={}", meeting.getId());
            }
        }
    }

    /** 🔹 모임장에게 승인 요청 전송 */
    private void askOrganizerForMeetingApproval(LightningMeeting meeting) {
        sendMeetingApprovalRequestToOrganizer(meeting);

        //  모임장 허가 로직 -> 추후 구현
//        // ✅ 모임장 응답 대기 (예: 5분)
//        LocalDateTime deadline = LocalDateTime.now().plusMinutes(5);
//        while (LocalDateTime.now().isBefore(deadline)) {
//            if (meeting.isApproved()) {  // 모임장 승인 처리
//                lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.SUCCESS);
//                return;
//            }
//            try {
//                Thread.sleep(10000);  // 10초 간격으로 상태 체크
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        // ✅ 응답이 없으면 자동 취소
//        lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.CANCELLED);
//        lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
//        sendMeetingCancelNotification(meeting);
//        log.info("모임장 승인 요청 후 응답이 없어 모임 자동 취소됨: MeetingId={}", meeting.getId());
    }

    private void sendMeetingApprovalRequestToOrganizer(LightningMeeting meeting) {
        User organizer = userService.getUserById(meeting.getOrganizerId());
        messageService.sendMessage(
                MessageDomainType.LIGHTNING_MEETING,
                LightningMeetingBusinessIdentifier.NOTIFICATION,
                organizer.getEmail(),
                meeting.getMeetingName() + " 모임을 진행하시겠습니까? (승인 / 연기)",
                null);
    }

    /** 🔹 모임 성사 조건 확인 */
    private boolean checkMeetingSuccess(LightningMeeting meeting) {
        if (meeting.getMeetingType() == MeetingType.CLASSICPAN) {
            return checkClassicParticipant(meeting);
        } else {
            return meeting.getMinPersonNum() <= getMeetingParticipantNum(meeting);
        }
    }

    private void sendMeetingSuccessNotification(LightningMeeting meeting) throws IOException {
        List<String> participantTokens = getParticipantTokens(meeting.getId());
//        for (String token : participantTokens) {
//            NotificationContent successNotification = LightningMeetingNotificationTemplateFactory.createMeetingSuccessNotification(meeting);
//            fcmService.sendNotification(token, successNotification);
//        }
//        log.info("모임 성사 알림 전송 완료: MeetingId={}", meeting.getId());
        for (String token : participantTokens) {
            try {
                NotificationContent successNotification = LightningMeetingNotificationTemplateFactory.createMeetingSuccessNotification(meeting);
                fcmService.sendNotification(token, successNotification, MessageDomainType.LIGHTNING_MEETING);
            } catch (IOException e) {
                log.error("성공 알림 전송 실패: MeetingId={}, Token={}, Error={}", meeting.getId(), token, e.getMessage());
            }
        }
        log.info("모임 성사 알림 전송 완료: MeetingId={}", meeting.getId());
    }

    private void sendMeetingCancelNotification(LightningMeeting meeting) throws IOException {
        List<String> participantTokens = getParticipantTokens(meeting.getId());

        for (String token : participantTokens) {
            NotificationContent cancelNotification = LightningMeetingNotificationTemplateFactory.createMeetingFailedNotification(meeting);
            fcmService.sendNotification(token, cancelNotification, MessageDomainType.LIGHTNING_MEETING);
        }
        log.info("모임 취소 알림 전송 완료: MeetingId={}", meeting.getId());
    }

    private List<String> getParticipantTokens(Long meetingId) {
        return lightningMeetingParticipantRepository.findAllParticipantsByMeetingId(meetingId).stream()
                .map(participant -> fcmRepository.findTokensByUserId(participant.getUserId()))
                .flatMap(List::stream)
                .filter(FCMToken::isValid)
                .map(FCMToken::getToken)
                .toList();
    }

    private Integer getMeetingParticipantNum(LightningMeeting meeting) {
        return lightningMeetingParticipantRepository.getMeetingParticipantNum(meeting.getId());
    }

    private boolean checkClassicParticipant(LightningMeeting meeting) {
        List<InstrumentAssignment> instrumentAssignmentList = meeting.getInstrumentAssignmentList();
        for (InstrumentAssignment instrumentAssignment : instrumentAssignmentList) {
            Integer currentInstrumentAssign = lightningMeetingInstrumentAssignmentRepository.getCurrentInstrumentAssign(instrumentAssignment.getInstrument());
            if (instrumentAssignment.getMinParticipants() > currentInstrumentAssign)
                return false;
        }
        return true;
    }

    private void cancelLightningMeeting(LightningMeeting meeting) {
        lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
        eventListener.notifyMeetingDeleted(meeting.getId());
    }

    private void startLightningMeeting(LightningMeeting meeting) {
        // 참가자들에게 번개 모임 진행 메시지 발송 (구현 필요)
    }
}

