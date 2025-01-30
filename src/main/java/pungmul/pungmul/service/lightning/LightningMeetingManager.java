package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.domain.lightning.MeetingType;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.domain.LightningMeetingBusinessIdentifier;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.message.MessageService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingManager {

    private final UserService userService;
    private final LightningMeetingRepository lightningMeetingRepository;
    private final LightningMeetingInstrumentManager lightningMeetingInstrumentManager;
    private final LightningMeetingParticipantService lightningMeetingParticipantService;
    private final LightningMeetingService lightningMeetingService;
    private final Map<String, GetNearLightningMeetingRequestDTO> userGetNearLightningMeetingData = new ConcurrentHashMap<>();
    private final LightningMeetingNotificationTrigger lightningMeetingNotificationTrigger;
    private final MessageService messageService;

@Transactional
public CreateLightningMeetingResponseDTO createLightningMeeting(CreateLightningMeetingRequestDTO requestDTO,
                                                                UserDetailsImpl userDetails) {
    // 1. 주최자 조회
    User organizer = userService.getUserByEmail(userDetails.getUsername());

    // 2. LightningMeeting 생성 (위임)
    LightningMeeting lightningMeeting = lightningMeetingService.createLightningMeeting(requestDTO, organizer);

    // 3. 신규 번개 모임 생성 이벤트 발생
    notifyGetNearLightningMeeting();

    // 4. CLASSICPAN 모임일 경우 악기 구성 정보 추가 (위임)
    if (lightningMeeting.getMeetingType().equals(MeetingType.CLASSICPAN)) {
        lightningMeetingInstrumentManager.createInstrumentAssign(requestDTO, lightningMeeting.getId());
    }

    // 5. 주최자를 번개 모임 참여자로 추가 (위임)
    AddLightningMeetingParticipantRequestDTO addParticipantDTO = lightningMeetingParticipantService.getAddLightningMeetingParticipantRequestDTO(lightningMeeting, requestDTO.getOrganizerInstrument());
    addLightningMeetingParticipant(addParticipantDTO, userDetails, true);

    // 6. 생성된 번개 모임 response 반환
    return lightningMeetingService.getCreateLightningMeetingResponseDTO(lightningMeeting);
}

    @Transactional
    public AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipant(AddLightningMeetingParticipantRequestDTO addLightningMeetingParticipantRequestDTO, UserDetailsImpl userDetails, Boolean organizer){
        // 1. 참가자 조회
        User user = userService.getUserByEmail(userDetails.getUsername());

        // 2. 모임 중복 참가 여부 검사
        lightningMeetingParticipantService.checkDuplicateParticipant(addLightningMeetingParticipantRequestDTO, user);

        // 3. 참여자 정보 생성
        LightningMeetingParticipant participant = lightningMeetingParticipantService.buildLightningMeetingParticipant(userDetails, addLightningMeetingParticipantRequestDTO, organizer);
        log.info("lightningMeetingParticipant: {}", participant);

        // 4. 참여자 정보 DB 저장
        lightningMeetingParticipantService.addLightningMeetingParticipant(participant);

        // 5. 참여자 악기 정보 모임 반영
        lightningMeetingInstrumentManager.increaseAssignment(
                participant.getMeetingId(), participant.getInstrumentAssigned());

        // 6. 모임 참여 접수 FCM 메세지 발송
        lightningMeetingNotificationTrigger.triggerAddParticipant(addLightningMeetingParticipantRequestDTO.getMeetingId(), userDetails);

        // 7. 신규 참여자 정보 갱신 이벤트 발생
        lightningMeetingParticipantService.notifyLightningMeetingParticipants(addLightningMeetingParticipantRequestDTO.getMeetingId());

        // 3. 참여자 정보 반환
        return AddLightningMeetingParticipantResponseDTO.builder()
                .lightningMeetingParticipant(participant)
                .build();
    }


    public void getNearLightningMeetings(GetNearLightningMeetingRequestDTO requestDTO, String username){
        // 1. 사용자 위치, 지도 레벨 데이터 기록
        userGetNearLightningMeetingData.put(username, requestDTO);

        // 2. 전체 번개 모임 조회
        List<LightningMeeting> allMeetings = lightningMeetingRepository.getAllLightningMeeting();

        // 3. 사용자 위치, 지도 레벨 기반 모임 필터링
        GetNearLightningMeetingResponseDTO lightningMeetingResponse = lightningMeetingService.getNearLightningMeetingFilter(requestDTO, allMeetings);

        // 4. 근처 번개 모임 stomp 메세지 발송
        messageService.sendMessage(MessageDomainType.LIGHTNING_MEETING,
                LightningMeetingBusinessIdentifier.NEARBY,
                username,
                lightningMeetingResponse);
    }

    public void getLightningMeetingParticipants(Long meetingId){
        lightningMeetingParticipantService.getLightningMeetingParticipants(meetingId);
    }

    private void notifyGetNearLightningMeeting() {
        log.info("call lightningMeeting update notification");
        userGetNearLightningMeetingData.forEach((username, requestDTO) -> getNearLightningMeetings(requestDTO, username));
    }


    public WithdrawLightningMeetingResponseDTO withdrawLightningMeeting(WithdrawLightningMeetingRequestDTO withdrawLightningMeetingRequestDTO, UserDetailsImpl userDetails) {
        return lightningMeetingParticipantService.withdrawLightningMeeting(withdrawLightningMeetingRequestDTO, userDetails);
    }
}
