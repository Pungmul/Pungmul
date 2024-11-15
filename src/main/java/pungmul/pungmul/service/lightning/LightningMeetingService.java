package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.domain.lightning.MeetingType;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LightningMeetingService {
    private final LightningMeetingRepository lightningMeetingRepository;
    private final LightningMeetingParticipantRepository lightningMeetingParticipantRepository;
    private final LightningMeetingInstrumentAssignmentRepository lightningMeetingInstrumentAssignmentRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final UserRepository userRepository;

    /**
     * 번개 모임 생성 로직.
     * 1. 요청한 사용자의 이메일(userDetails)을 통해 주최자 ID를 조회.
     * 2. 요청 DTO와 주최자 ID를 기반으로 LightningMeeting 객체 생성.
     * 3. LightningMeeting 정보를 DB에 저장.
     * 4. 모임 유형(MeetingType)이 CLASSICPAN일 경우 악기 구성 정보를 추가.
     * 5. 모임 주최자를 번개 모임 참여자로 추가.
     * 6. 생성된 LightningMeeting 정보를 반환.
     */
    @Transactional
    public CreateLightningMeetingResponseDTO createLightningMeeting(CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO,
                                                                    UserDetails userDetails) {

        // 1. 주최자 ID 조회
        Long organizerId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        // 2. LightningMeeting 객체 생성
        LightningMeeting lightningMeeting = getLightningMeeting(createLightningMeetingRequestDTO, organizerId);

        // 3. DB에 LightningMeeting 저장
        lightningMeetingRepository.createLightningMeeting(lightningMeeting);

        // 4. CLASSICPAN 모임일 경우 악기 구성 정보 추가
        if (lightningMeeting.getMeetingType().equals(MeetingType.CLASSICPAN)) {
            createInstrumentAssign(createLightningMeetingRequestDTO, lightningMeeting.getId());
        }

        // 5. 주최자를 번개 모임 참여자로 추가
        addLightningMeetingParticipant(userDetails, getAddLightningMeetingRequestDTO(lightningMeeting, createLightningMeetingRequestDTO.getOrganizerInstrument()), true);

        // 6. 생성된 LightningMeeting 정보 반환
        return getCreateLightningMeetingResponseDTO(lightningMeeting);
    }

    /**
     * 번개 모임 참여자 추가 로직.
     * 1. 요청 정보를 기반으로 LightningMeetingParticipant 객체 생성.
     * 2. DB에 참여자 정보 저장.
     * 3. 추가된 참여자 정보를 포함한 응답 DTO 반환.
     */
    @Transactional
    public AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipant(UserDetails userDetails, AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, Boolean isOrganizer) {
        // 1. 참여자 정보 생성
        LightningMeetingParticipant lightningMeetingParticipant = getLightningMeetingParticipant(userDetails, addLightningMeetingRequestDTO, isOrganizer);

        // 2. 참여자 정보 DB 저장
        lightningMeetingParticipantRepository.addLightningMeetingParticipant(lightningMeetingParticipant);

        lightningMeetingInstrumentAssignmentRepository
                .increaseAssignment(lightningMeetingParticipant.getMeetingId(),
                        lightningMeetingParticipant.getInstrumentAssigned());

        // 3. 참여자 정보 반환
        return AddLightningMeetingParticipantResponseDTO.builder()
                .lightningMeetingParticipant(lightningMeetingParticipant)
                .build();
    }





    /**
     * CLASSICPAN 모임의 악기 배정 정보를 생성.
     * 1. 요청 DTO에 포함된 악기 배정 리스트를 순회.
     * 2. 각 악기 배정 정보를 InstrumentAssignment 객체로 변환.
     * 3. InstrumentAssignment 정보를 DB에 저장.
     */
    private void createInstrumentAssign(CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO, Long meetingId) {
        List<InstrumentAssignRequestDTO> instrumentAssignRequestDTOList = createLightningMeetingRequestDTO.getInstrumentAssignRequestDTOList();
        for (InstrumentAssignRequestDTO assignRequestDTO : instrumentAssignRequestDTOList) {
            InstrumentAssignment instrumentAssignment = InstrumentAssignment.builder()
                    .instrument(assignRequestDTO.getInstrument())
                    .meetingId(meetingId)
                    .minParticipants(assignRequestDTO.getMinPersonNum())
                    .maxParticipants(assignRequestDTO.getMaxPersonNum())
                    .build();

            lightningMeetingInstrumentAssignmentRepository.createAssignment(instrumentAssignment);
        }
    }

    /**
     * 번개 모임 생성 시 주최자 정보를 기반으로 AddLightningMeetingParticipantRequestDTO 생성.
     * @param lightningMeeting 생성된 번개 모임 객체.
     * @param organizerInstrument 주최자 악기.
     * @return AddLightningMeetingParticipantRequestDTO 객체.
     */
    private AddLightningMeetingParticipantRequestDTO getAddLightningMeetingRequestDTO(LightningMeeting lightningMeeting,Instrument organizerInstrument) {
        return AddLightningMeetingParticipantRequestDTO.builder()
                .meetingId(lightningMeeting.getId())
                .instrument(organizerInstrument)
                .build();
    }

    /**
     * 번개 모임 참여자 객체 생성 로직.
     * 1. 사용자 이메일(userDetails)로 사용자 정보를 조회.
     * 2. 주 악기를 포함한 LightningMeetingParticipant 객체 생성.
     */
    public LightningMeetingParticipant getLightningMeetingParticipant(UserDetails userDetails, AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, Boolean isOrganizer) {
        // 1. 사용자 정보 조회
        User user = userRepository.getUserByEmail(userDetails.getUsername()).orElseThrow(NoSuchElementException::new);

        Instrument instrument = addLightningMeetingRequestDTO.getInstrument() != null
                ? addLightningMeetingRequestDTO.getInstrument()
                : instrumentStatusRepository.getMajorInstrumentByUserId(user.getId());

        // 2. LightningMeetingParticipant 객체 생성
        return LightningMeetingParticipant.builder()
                .meetingId(addLightningMeetingRequestDTO.getMeetingId())
                .userId(user.getId())
                .username(userDetails.getUsername())
                .instrumentAssigned(instrument) // 사용자 주 악기
                .organizer(isOrganizer)
                .build();
    }

    /**
     * 생성된 번개 모임 정보를 반환하는 DTO 생성.
     * @param lightningMeeting 생성된 번개 모임 객체.
     * @return CreateLightningMeetingResponseDTO 객체.
     */
    private CreateLightningMeetingResponseDTO getCreateLightningMeetingResponseDTO(LightningMeeting lightningMeeting) {
        return CreateLightningMeetingResponseDTO.builder()
                .lightningMeetingId(lightningMeeting.getId())
                .lightningMeetingName(lightningMeeting.getMeetingName())
                .organizerName(userRepository.getUserByUserId(lightningMeeting.getOrganizerId())
                        .map(User::getName)
                        .orElseThrow(NoSuchElementException::new))
                .build();
    }

    /**
     * CreateLightningMeetingRequestDTO와 organizerId를 기반으로 LightningMeeting 객체 생성.
     * @param createLightningMeetingRequestDTO 요청 DTO.
     * @param organizerId 주최자 ID.
     * @return LightningMeeting 객체.
     */
    private static LightningMeeting getLightningMeeting(CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO, Long organizerId) {
        return LightningMeeting.builder()
                .meetingName(createLightningMeetingRequestDTO.getMeetingName())
                .meetingDescription(createLightningMeetingRequestDTO.getMeetingDescription())
                .startTime(createLightningMeetingRequestDTO.getStartTime())
                .endTime(createLightningMeetingRequestDTO.getEndTime())
                .minPersonNum(createLightningMeetingRequestDTO.getMinPersonNum())
                .maxPersonNum(createLightningMeetingRequestDTO.getMaxPersonNum())
                .organizerId(organizerId)
                .meetingType(createLightningMeetingRequestDTO.getMeetingType())
                .latitude(createLightningMeetingRequestDTO.getLatitude())
                .longitude(createLightningMeetingRequestDTO.getLongitude())
                .build();
    }
}


