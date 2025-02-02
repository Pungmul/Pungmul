package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.lightning.*;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.domain.message.NotificationContent;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.repository.message.repository.FCMRepository;
import pungmul.pungmul.service.message.FCMService;
import pungmul.pungmul.service.message.template.LightningMeetingNotificationTemplateFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingStatus {

    private final LightningMeetingRepository lightningMeetingRepository;
    private final LightningMeetingParticipantRepository lightningMeetingParticipantRepository;
    private final FCMService fcmService;
    private final FCMRepository fcmRepository;
    private final LightningMeetingInstrumentAssignmentRepository lightningMeetingInstrumentAssignmentRepository;
    private final LightningMeetingEventListener eventListener;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void processMeetingsAndCheckConditions() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        // 모집 기간이 만료된 OPEN 상태의 모임 조회
        List<LightningMeeting> expiredMeetings = lightningMeetingRepository.findAllByDeadlineAndStatus(now, pungmul.pungmul.domain.lightning.LightningMeetingStatus.OPEN);
//        log.info("Expired meetings to process: {}", expiredMeetings.size());

        // 모집 기간 중 성사 조건을 만족하는 모임 조회
        List<LightningMeeting> successfulMeetings = lightningMeetingRepository.findAllMeetingWithEnoughParticipants(now, pungmul.pungmul.domain.lightning.LightningMeetingStatus.OPEN);
//        log.info("Successful meetings in progress: {}", successfulMeetings.size());

        // 모집 기간이 만료된 모임 처리
        for (LightningMeeting meeting : expiredMeetings) {
            processExpiredMeeting(meeting);
        }

        // 성사 조건을 만족한 모집 기간 중 모임 처리
        for (LightningMeeting meeting : successfulMeetings) {
            askOrganizerForMeetingApproval(meeting);
        }
    }

    // 모집 기간이 만료된 모임 처리
    private void processExpiredMeeting(LightningMeeting meeting) throws IOException {
        boolean isMeetingSuccessful = checkMeetingSuccess(meeting);

        if (isMeetingSuccessful) {
            startLightningMeeting(meeting);
            lightningMeetingRepository.setStatus(meeting.getId(), pungmul.pungmul.domain.lightning.LightningMeetingStatus.SUCCESS);
            sendMeetingSuccessNotification(meeting);
        } else {
            cancelLightningMeeting(meeting);
            lightningMeetingRepository.setStatus(meeting.getId(), pungmul.pungmul.domain.lightning.LightningMeetingStatus.CANCELLED);
            sendMeetingCancelNotification(meeting);
        }

        lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());
    }

    // 모임장에게 승인 요청 전송
    private void askOrganizerForMeetingApproval(LightningMeeting meeting) {
//        log.info("Requesting organizer's approval for meeting: {}", meeting.getId());

        // 메시지 전송 (예: WebSocket 사용)
//        sendMeetingApprovalRequestToOrganizer(meeting);

        // 상태 변경 대기 (모임장의 응답에 따라 변경)
    }

    // 모임 성사 조건 확인
    private boolean checkMeetingSuccess(LightningMeeting meeting) {
        if (meeting.getMeetingType() == MeetingType.CLASSICPAN) {
            return checkClassicParticipant(meeting);
        } else {
            return meeting.getMinPersonNum() <= getMeetingParticipantNum(meeting);
        }
    }

    //    // 모임장에게 메시지 전송 메서드 (구현 필요)
//    private void sendMeetingApprovalRequestToOrganizer(LightningMeeting meeting) {
//        // WebSocket 메시지 전송 로직
//        messageService.sendToOrganizer(
//                meeting.getOrganizerId(),
//                "모임 성사 인원이 충족되었습니다. 모임 진행 여부를 결정해주세요.",
//                meeting.getId()
//        );
//    }

    private void sendMeetingSuccessNotification(LightningMeeting meeting) throws IOException {
        List<String> participantTokens = getParticipantTokens(meeting.getId());

        for (String token : participantTokens) {
            NotificationContent successNotification = LightningMeetingNotificationTemplateFactory.createMeetingSuccessNotification(meeting);
            fcmService.sendNotification(token, successNotification);
        }
        log.info("모임 성사 알림 전송 완료: MeetingId={}", meeting.getId());
    }

    private void sendMeetingCancelNotification(LightningMeeting meeting) throws IOException {
        List<String> participantTokens = getParticipantTokens(meeting.getId());

        for (String token : participantTokens) {
            NotificationContent cancelNotification = LightningMeetingNotificationTemplateFactory.createMeetingFailedNotification(meeting);
            fcmService.sendNotification(token, cancelNotification);
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

    @Scheduled(fixedRate = 60000) // 1분 간격으로 실행
    public void sendMeetingReminders() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        // 30분 내에 시작하는 SUCCESS 상태의 모임 조회
        List<LightningMeeting> meetings = lightningMeetingRepository.findMeetingsStartingInThirtyMinutes(now, thirtyMinutesLater);

        for (LightningMeeting meeting : meetings) {
            sendRemindersForMeeting(meeting);
        }
    }

    private void sendRemindersForMeeting(LightningMeeting meeting) throws IOException {
        // 해당 모임의 참여자 조회
        List<LightningMeetingParticipant> participants = lightningMeetingParticipantRepository.findAllParticipantsByMeetingId(meeting.getId());
        List<String> participantTokens = getParticipantTokens(meeting.getId());

        NotificationContent notificationContent = LightningMeetingNotificationTemplateFactory.remindMeetingNotification(meeting);
        for (String token : participantTokens) {
            fcmService.sendNotification(token, notificationContent);
        }
    }

    private Integer getMeetingParticipantNum(LightningMeeting meeting) {
        return lightningMeetingParticipantRepository.getMeetingParticipantNum(meeting.getId());
    }

    private boolean checkClassicParticipant(LightningMeeting meeting) {
        //  해당 정식판굿의 악기 구성 제한 정보
        List<InstrumentAssignment> instrumentAssignmentList = meeting.getInstrumentAssignmentList();
        //  모든 악기 제한에 대해
        for (InstrumentAssignment instrumentAssignment : instrumentAssignmentList) {
            //  현재 해당 악기로 가입한 사용자 수
            Integer currentInstrumentAssign = lightningMeetingInstrumentAssignmentRepository.getCurrentInstrumentAssign(instrumentAssignment.getInstrument());
            //  악기 조건을 충족하지 못하면 false
            if (instrumentAssignment.getMinParticipants() > currentInstrumentAssign)
                return false;
        }
        return true;
    }

    private void cancelLightningMeeting(LightningMeeting meeting) {
        //  참가자들에게 번개 모임 취소 메세지 발송

        //  해당 모임의 모든 사용자 INACTIVE
        lightningMeetingParticipantRepository.inactivateMeetingParticipants(meeting.getId());

        eventListener.notifyMeetingDeleted(meeting.getId());
    }

    private void startLightningMeeting(LightningMeeting meeting) {
        //  참가자들에게 번개 모임 진행 메세지 발송

    }
}
