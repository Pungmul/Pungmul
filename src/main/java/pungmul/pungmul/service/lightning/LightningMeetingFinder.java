//package pungmul.pungmul.service.lightning;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LightningMeetingFinder {
//    private final LightningMeetingRepository lightningMeetingRepository;
//
//    public List<LightningMeeting> findNearbyMeetings(GetNearLightningMeetingRequestDTO requestDTO) {
//        List<LightningMeeting> allMeetings = lightningMeetingRepository.getAllLightningMeeting();
//        return allMeetings.stream()
//                .filter(meeting -> calculateDistance(requestDTO.getLatitude(), requestDTO.getLongitude(), meeting.getLatitude(), meeting.getLongitude()) <= MapLevelDistance.getDistanceByLevel(requestDTO.getMapLevel()))
//                .toList();
//    }
//}
