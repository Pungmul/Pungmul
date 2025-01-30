package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.MapLevelDistance;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static pungmul.pungmul.core.geo.DistanceCalculator.calculateDistance;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingService {
    private final LightningMeetingRepository lightningMeetingRepository;
    private final UserService userService;

    public LightningMeeting createLightningMeeting(CreateLightningMeetingRequestDTO requestDTO, User organizer) {
        LightningMeeting lightningMeeting = getLightningMeeting(requestDTO, organizer.getId());
        lightningMeetingRepository.createLightningMeeting(lightningMeeting);
        return lightningMeeting;
    }

    private static LightningMeeting getLightningMeeting(CreateLightningMeetingRequestDTO requestDTO, Long organizerId) {
        log.info("recruitmentEndTime:{}", requestDTO.getRecruitmentEndTime().toLocalTime());
        return LightningMeeting.builder()
                .meetingName(requestDTO.getMeetingName())
                .meetingDescription(requestDTO.getMeetingDescription())
                .recruitmentEndTime(requestDTO.getRecruitmentEndTime())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .minPersonNum(requestDTO.getMinPersonNum())
                .maxPersonNum(requestDTO.getMaxPersonNum())
                .organizerId(organizerId)
                .meetingType(requestDTO.getMeetingType())
                .latitude(requestDTO.getLatitude())
                .longitude(requestDTO.getLongitude())
                .build();
    }

    /**
     * 생성된 번개 모임 정보를 반환하는 DTO 생성.
     * @param lightningMeeting 생성된 번개 모임 객체.
     * @return CreateLightningMeetingResponseDTO 객체.
     */
    public CreateLightningMeetingResponseDTO getCreateLightningMeetingResponseDTO(LightningMeeting lightningMeeting) {
        return CreateLightningMeetingResponseDTO.builder()
                .lightningMeetingId(lightningMeeting.getId())
                .lightningMeetingName(lightningMeeting.getMeetingName())
                .organizerName(userService.getUserById(lightningMeeting.getOrganizerId()).getName())
                .build();
    }
//    public GetNearLightningMeetingResponseDTO getNearLightningMeetingFilter(GetNearLightningMeetingRequestDTO requestDTO, List<LightningMeeting> allMeetings) {
//        Double userLatitude = requestDTO.getLatitude();
//        Double userLongitude = requestDTO.getLongitude();
//        Integer mapLevel = requestDTO.getMapLevel();
//
//        // 지도 레벨에 따른 거리 계산 (미터)
//        int distanceThreshold = MapLevelDistance.getDistanceByLevel(mapLevel) * 2;
//
//        // 번개 모임 필터링
//        GetNearLightningMeetingResponseDTO lightningMeetingResponse = GetNearLightningMeetingResponseDTO.builder()
//                .lightningMeetingList(allMeetings.stream()
//                        .filter(meeting -> {
//                            // 사용자 위치와 모임 위치 간 거리 계산
//                            double distance = calculateDistance(
//                                    userLatitude,
//                                    userLongitude,
//                                    meeting.getLatitude(),
//                                    meeting.getLongitude()
//                            );
//                            return distance <= distanceThreshold;
//                        })
//                        .collect(Collectors.toList()))
//                .build();
//        return lightningMeetingResponse;
//    }

    public GetNearLightningMeetingResponseDTO getNearLightningMeetingFilter(GetNearLightningMeetingRequestDTO requestDTO, List<LightningMeeting> allMeetings) {
        Double userLatitude = requestDTO.getLatitude();
        Double userLongitude = requestDTO.getLongitude();
        Integer mapLevel = requestDTO.getMapLevel();

        // 지도 레벨에 따른 거리 계산 (미터)
        int distanceThreshold = MapLevelDistance.getDistanceByLevel(mapLevel) * 2;

        // 번개 모임 필터링 및 주최자 정보 추가
        List<LightningMeetingWithOrganizerDTO> meetingWithOrganizerList = allMeetings.stream()
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
                .map(meeting -> {
                    // 주최자 정보 조회
                    User organizer = userService.getUserById(meeting.getOrganizerId());
                    return LightningMeetingWithOrganizerDTO.builder()
                            .lightningMeeting(meeting)
                            .organizerName(organizer.getName())
                            .build();
                })
                .collect(Collectors.toList());

        return GetNearLightningMeetingResponseDTO.builder()
                .lightningMeetingList(meetingWithOrganizerList)
                .build();
    }
}
