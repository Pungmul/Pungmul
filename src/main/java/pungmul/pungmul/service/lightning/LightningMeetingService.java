package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.*;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static pungmul.pungmul.core.geo.DistanceCalculator.calculateDistance;

@Slf4j
@RequiredArgsConstructor
@Service
public class LightningMeetingService {
    private final LightningMeetingRepository lightningMeetingRepository;
    private final LightningMeetingParticipantRepository lightningMeetingParticipantRepository;
    private final LightningMeetingInstrumentAssignmentRepository lightningMeetingInstrumentAssignmentRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final UserRepository userRepository;
    private final LightningMeetingNotificationTrigger lightningMeetingNotificationTrigger;

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
                                                                    UserDetailsImpl userDetails) {

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
    public AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipant(UserDetailsImpl userDetails, AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, Boolean isOrganizer) {
        // 1. 참여자 정보 생성
        LightningMeetingParticipant lightningMeetingParticipant = getLightningMeetingParticipant(userDetails, addLightningMeetingRequestDTO, isOrganizer);

        // 2. 참여자 정보 DB 저장
        lightningMeetingParticipantRepository.addLightningMeetingParticipant(lightningMeetingParticipant);

        lightningMeetingInstrumentAssignmentRepository
                .increaseAssignment(lightningMeetingParticipant.getMeetingId(),
                        lightningMeetingParticipant.getInstrumentAssigned());

        lightningMeetingNotificationTrigger.triggerAddParticipant(addLightningMeetingRequestDTO.getMeetingId(), userDetails);

        // 3. 참여자 정보 반환
        return AddLightningMeetingParticipantResponseDTO.builder()
                .lightningMeetingParticipant(lightningMeetingParticipant)
                .build();
    }


    /**
     * 사용자 위치와 지도 레벨을 기준으로 주변 번개 모임 데이터를 필터링하여 반환하는 메소드.
     *
     * @param requestDTO 사용자의 현재 위치(위도, 경도)와 지도 레벨 정보를 담은 요청 DTO
     * @return 사용자 위치와 지도 레벨을 기준으로 필터링된 번개 모임 리스트를 포함한 응답 DTO
     *
     * 로직 동작 과정:
     * 1. 사용자로부터 요청된 위도(latitude), 경도(longitude), 지도 레벨(mapLevel)을 가져옵니다.
     * 2. 데이터베이스에서 모든 번개 모임 정보를 조회합니다.
     * 3. 지도 레벨에 따라 검색 반경(distanceThreshold)을 계산합니다.
     *    - 지도 레벨에 따른 거리(MapLevelDistance)를 가져와 반경 * 2로 설정.
     * 4. 모든 번개 모임 중에서 사용자 위치와 번개 모임 위치 간 거리를 계산하여 필터링합니다.
     *    - 거리 계산 로직(calculateDistance)을 사용하여 두 위치 간 거리를 구합니다.
     *    - 거리 값이 설정된 검색 반경 이내일 경우 결과에 포함합니다.
     * 5. 필터링된 번개 모임 리스트를 응답 DTO에 담아 반환합니다.
     */
    public GetNearLightningMeetingResponseDTO getNearLightningMeetings(GetNearLightningMeetingRequestDTO requestDTO) {

        Double userLatitude = requestDTO.getLatitude();
        Double userLongitude = requestDTO.getLongitude();
        Integer mapLevel = requestDTO.getMapLevel();

        List<LightningMeeting> allMeetings = lightningMeetingRepository.getAllLightningMeeting();

        // 지도 레벨에 따른 거리 계산 (미터)
        int distanceThreshold = MapLevelDistance.getDistanceByLevel(mapLevel) * 2;

        // 번개 모임 필터링
        return GetNearLightningMeetingResponseDTO.builder()
                        .lightningMeetingList(allMeetings.stream()
                            .filter(meeting -> {
                                // 사용자 위치와 모임 위치 간 거리 계산
                                double distance = calculateDistance(
                                        userLatitude,
                                        userLongitude,
                                        meeting.getLatitude(),
                                        meeting.getLongitude()
                                );
                                return distance <= distanceThreshold;
                                })
                            .collect(Collectors.toList()))
                        .build();
    }

    public GetMeetingParticipantsResponseDTO getMeetingParticipants(GetMeetingParticipantsRequestDTO getMeetingParticipantsRequestDTO) {
        Long meetingId = getMeetingParticipantsRequestDTO.getMeetingId();
        List<LatLong> meetingParticipants = lightningMeetingParticipantRepository.getMeetingParticipantLocations(meetingId);
        log.info(meetingParticipants.toString());
        return GetMeetingParticipantsResponseDTO.builder()
                .locations(meetingParticipants)
                .build();
    }

    @Scheduled(fixedRate = 60000)
    public void checkMeetingDeadline(){
        //  모집 시간이 지난 번개 미팅 리스트 반환
        List<LightningMeeting> allByDeadline = lightningMeetingRepository.findAllByDeadline(LocalDateTime.now());

        for (LightningMeeting meeting : allByDeadline) {
            //  정식 판굿인 경우
            if (meeting.getMeetingType() == MeetingType.CLASSICPAN) {
                if (checkClassicParticipant(meeting))
                    startLightningMeeting(meeting);
                else
                    cancelLightningMeeting(meeting);
            }
            //  정식 판굿이 아닌 경우
            else {
                if (meeting.getMinPersonNum() > getMeetingParticipantNum(meeting))
                    startLightningMeeting(meeting);
                else
                    cancelLightningMeeting(meeting);
            }
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
    }

    private void startLightningMeeting(LightningMeeting meeting) {
        //  참가자들에게 번개 모임 진행 메세지 발송

    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void processExpiredMeetings() {
        List<LightningMeeting> expiredMeetings = lightningMeetingRepository.findAllByDeadline(LocalDateTime.now());

        // 모든 참가자의 status를 INACTIVE로 변경
        expiredMeetings.stream().map(LightningMeeting::getId)
                .forEach(lightningMeetingParticipantRepository::inactivateMeetingParticipants);
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
    public LightningMeetingParticipant getLightningMeetingParticipant(UserDetailsImpl userDetails, AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO, Boolean isOrganizer) {
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
                .location(LatLong.builder()
                        .latitude(addLightningMeetingRequestDTO.getLatitude())
                        .longitude(addLightningMeetingRequestDTO.getLongitude())
                        .build())
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
        log.info("recruitmentEndTime:{}",createLightningMeetingRequestDTO.getRecruitmentEndTime().toLocalTime());
        return LightningMeeting.builder()
                .meetingName(createLightningMeetingRequestDTO.getMeetingName())
                .meetingDescription(createLightningMeetingRequestDTO.getMeetingDescription())
                .recruitmentEndTime(createLightningMeetingRequestDTO.getRecruitmentEndTime())
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


