//package pungmul.pungmul.service.lightning;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import pungmul.pungmul.config.security.UserDetailsImpl;
//import pungmul.pungmul.domain.lightning.LightningMeeting;
//import pungmul.pungmul.domain.lightning.MeetingType;
//import pungmul.pungmul.dto.lightning.AddLightningMeetingParticipantRequestDTO;
//import pungmul.pungmul.dto.lightning.CreateLightningMeetingRequestDTO;
//import pungmul.pungmul.dto.lightning.CreateLightningMeetingResponseDTO;
//import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LightningMeetingServiceManager {
//    private final LightningMeetingRepository lightningMeetingRepository;
//    private final LightningMeetingParticipantManager participantManager;
//    private final LightningMeetingNotificationService notificationService;
//    private final LightningMeetingInstrumentManager instrumentManager;
//
//    @Transactional
//    public CreateLightningMeetingResponseDTO createLightningMeeting(CreateLightningMeetingRequestDTO requestDTO, UserDetailsImpl userDetails) {
//        // 1. 주최자 ID 조회
//        Long organizerId = lightningMeetingRepository.getOrganizerIdByEmail(userDetails.getUsername())
//                .orElseThrow(NoSuchElementException::new);
//
//        // 2. LightningMeeting 생성
//        LightningMeeting meeting = LightningMeeting.builder()
//                .meetingName(requestDTO.getMeetingName())
//                .meetingDescription(requestDTO.getMeetingDescription())
//                .recruitmentEndTime(requestDTO.getRecruitmentEndTime())
//                .startTime(requestDTO.getStartTime())
//                .endTime(requestDTO.getEndTime())
//                .minPersonNum(requestDTO.getMinPersonNum())
//                .maxPersonNum(requestDTO.getMaxPersonNum())
//                .organizerId(organizerId)
//                .meetingType(requestDTO.getMeetingType())
//                .latitude(requestDTO.getLatitude())
//                .longitude(requestDTO.getLongitude())
//                .build();
//
//        // 3. DB 저장
//        lightningMeetingRepository.createLightningMeeting(meeting);
//
//        // 4. CLASSICPAN 모임인 경우 악기 구성 추가
//        if (requestDTO.getMeetingType() == MeetingType.CLASSICPAN) {
//            instrumentManager.createInstrumentAssign(requestDTO, meeting.getId());
//        }
//
//        // 5. 주최자를 참여자로 추가
//        participantManager.addLightningMeetingParticipant(
//                userDetails,
//                AddLightningMeetingParticipantRequestDTO.builder()
//                        .latitude(requestDTO.getLatitude())
//                        .longitude(requestDTO.getLongitude())
//                        .meetingId(meeting.getId())
//                        .instrument(requestDTO.getOrganizerInstrument())
//                        .build(),
//                true
//        );
//
//        // 6. 응답 반환
//        return CreateLightningMeetingResponseDTO.builder()
//                .lightningMeetingId(meeting.getId())
//                .lightningMeetingName(meeting.getMeetingName())
//                .organizerName(userDetails.getUsername())
//                .build();
//    }
//
//    @Scheduled(fixedRate = 60000) // 1분마다 실행
//    @Transactional
//    public void processMeetingsAndCheckConditions() {
//        LocalDateTime now = LocalDateTime.now();
//
//        // 모집 마감된 OPEN 상태의 모임
//        List<LightningMeeting> expiredMeetings = lightningMeetingRepository.findAllByDeadlineAndStatus(now, LightningMeetingStatus.OPEN);
//
//        // 조건 충족한 모임
//        List<LightningMeeting> successfulMeetings = lightningMeetingRepository.findAllMeetingWithEnoughParticipants(now, LightningMeetingStatus.OPEN);
//
//        for (LightningMeeting meeting : expiredMeetings) {
//            processExpiredMeeting(meeting);
//        }
//
//        for (LightningMeeting meeting : successfulMeetings) {
//            notificationService.askOrganizerForMeetingApproval(meeting);
//        }
//    }
//
//    private void processExpiredMeeting(LightningMeeting meeting) {
//        boolean success = participantManager.isMeetingSuccessful(meeting);
//
//        if (success) {
//            lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.SUCCESS);
//            notificationService.sendMeetingSuccessNotification(meeting);
//        } else {
//            lightningMeetingRepository.setStatus(meeting.getId(), LightningMeetingStatus.CANCELLED);
//            notificationService.sendMeetingCancelNotification(meeting);
//        }
//
//        participantManager.inactivateParticipants(meeting.getId());
//    }
//}
