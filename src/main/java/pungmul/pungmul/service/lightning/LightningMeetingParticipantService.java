package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.meeting.AlreadyInAnotherMeetingException;
import pungmul.pungmul.core.exception.custom.meeting.AlreadyJoinedParticipantException;
import pungmul.pungmul.core.exception.custom.meeting.NotJoinedLightningMeetingUser;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.domain.LightningMeetingBusinessIdentifier;
import pungmul.pungmul.dto.lightning.AddLightningMeetingParticipantRequestDTO;
import pungmul.pungmul.dto.lightning.WithdrawLightningMeetingRequestDTO;
import pungmul.pungmul.dto.lightning.WithdrawLightningMeetingResponseDTO;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;
import pungmul.pungmul.repository.member.impl.MybatisInstrumentStatusRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.message.MessageService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingParticipantService {
    private final UserService userService;
    private final MessageService messageService;
    private final LightningMeetingParticipantRepository participantRepository;
    private final LightningMeetingParticipantRepository lightningMeetingParticipantRepository;
    private final MybatisInstrumentStatusRepository instrumentStatusRepository;

    public void addLightningMeetingParticipant(LightningMeetingParticipant participant){
        lightningMeetingParticipantRepository.addLightningMeetingParticipant(participant);
    }

    public WithdrawLightningMeetingResponseDTO withdrawLightningMeeting(WithdrawLightningMeetingRequestDTO withdrawLightningMeetingRequestDTO, UserDetailsImpl userDetails) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        Long meetingId = withdrawLightningMeetingRequestDTO.getLightningMeetingId();

        // 🔹 사용자가 모임에 참가 중인지 확인
        if (!participantRepository.isUserParticipant(meetingId, userId))
            throw new NotJoinedLightningMeetingUser();

        // 🔹 참가자의 status를 inactive로 업데이트
        participantRepository.withdrawLightningMeeting(meetingId, userId);

        // 모임 참가자 정보 갱신
        getLightningMeetingParticipants(meetingId);

        return new WithdrawLightningMeetingResponseDTO("번개 모임에서 성공적으로 탈퇴하였습니다.");
    }

    public void checkDuplicateParticipant(AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, User user) {
        // 1. 해당 모임에 참가중인 사용자인지 검사
        boolean isAlreadyParticipant = lightningMeetingParticipantRepository.isUserAlreadyParticipant(
                addLightningMeetingRequestDTO.getMeetingId(), user.getId()
        );
        if (isAlreadyParticipant)
            throw new AlreadyJoinedParticipantException();

        // 2. 다른 모임에 이미 참가중인 사용자인지 검사
        boolean isInActiveMeeting = lightningMeetingParticipantRepository.isUserInActiveMeeting(user.getId());
        if (isInActiveMeeting)
            throw new AlreadyInAnotherMeetingException();
    }

    /**
     * 번개 모임 참여자 객체 생성 로직.
     * 1. 사용자 이메일(userDetails)로 사용자 정보를 조회.
     * 2. 주 악기를 포함한 LightningMeetingParticipant 객체 생성.
     */
    public LightningMeetingParticipant buildLightningMeetingParticipant(UserDetailsImpl userDetails, AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, Boolean isOrganizer) {
        // 1. 사용자 정보 조회
        User user = userService.getUserByEmail(userDetails.getUsername());
        log.info("getLightningParticipant : {}", user.getEmail());

        Instrument instrument = addLightningMeetingRequestDTO.getInstrument() != null
                ? addLightningMeetingRequestDTO.getInstrument()
                : instrumentStatusRepository.getMajorInstrumentByUserId(user.getId());

        log.info("lat : {}", addLightningMeetingRequestDTO.getLatitude());
        // 2. LightningMeetingParticipant 객체 생성
        return LightningMeetingParticipant.builder()
                .meetingId(addLightningMeetingRequestDTO.getMeetingId())
                .userId(user.getId())
                .username(userDetails.getUsername())
                .instrumentAssigned(instrument) // 사용자 주 악기
                .organizer(isOrganizer)
                .location(LatLong.builder()
                        .latitude(addLightningMeetingRequestDTO.getLatitude())
                        .longitude(addLightningMeetingRequestDTO.getLongitude())
                        .build())
                .build();
    }

    public void notifyLightningMeetingParticipants(Long meetingId) {
        log.info("call lightningmeeting participants update notification");
        getLightningMeetingParticipants(meetingId);
    }

    public void getLightningMeetingParticipants(Long meetingId) {
        log.info("meetingId : {}", meetingId);
        List<LatLong> meetingParticipants = lightningMeetingParticipantRepository.getMeetingParticipantLocations(meetingId);
        log.info("meeting participants : {}", meetingParticipants.toString());

        messageService.sendMessage(
                MessageDomainType.LIGHTNING_MEETING,
                LightningMeetingBusinessIdentifier.PARTICIPANTS,
                meetingId.toString(),
                meetingParticipants
        );
    }

    public int getMeetingParticipantNum(Long meetingId) {
        return participantRepository.getMeetingParticipantNum(meetingId);
    }

    /**
     * 번개 모임 생성 시 주최자 정보를 기반으로 AddLightningMeetingParticipantRequestDTO 생성.
     * @param lightningMeeting 생성된 번개 모임 객체.
     * @param organizerInstrument 주최자 악기.
     * @return AddLightningMeetingParticipantRequestDTO 객체.
     */
    public AddLightningMeetingParticipantRequestDTO getAddLightningMeetingParticipantRequestDTO(LightningMeeting lightningMeeting, Instrument organizerInstrument) {
        return AddLightningMeetingParticipantRequestDTO.builder()
                .latitude(lightningMeeting.getLatitude())
                .longitude(lightningMeeting.getLongitude())
                .meetingId(lightningMeeting.getId())
                .instrument(organizerInstrument)
                .build();
    }
}
