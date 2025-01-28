//package pungmul.pungmul.service.lightning;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LightningMeetingParticipantManager {
//    private final LightningMeetingParticipantRepository participantRepository;
//    private final LightningMeetingInstrumentAssignmentRepository instrumentAssignmentRepository;
//
//    @Transactional
//    public AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipant(UserDetailsImpl userDetails, AddLightningMeetingParticipantRequestDTO requestDTO, Boolean isOrganizer) {
//        if (participantRepository.isUserAlreadyParticipant(requestDTO.getMeetingId(), userDetails.getId())) {
//            throw new AlreadyJoinedParticipantException();
//        }
//
//        LightningMeetingParticipant participant = LightningMeetingParticipant.builder()
//                .meetingId(requestDTO.getMeetingId())
//                .userId(userDetails.getId())
//                .username(userDetails.getUsername())
//                .instrumentAssigned(requestDTO.getInstrument())
//                .organizer(isOrganizer)
//                .location(new LatLong(requestDTO.getLatitude(), requestDTO.getLongitude()))
//                .build();
//
//        participantRepository.addLightningMeetingParticipant(participant);
//
//        instrumentAssignmentRepository.increaseAssignment(requestDTO.getMeetingId(), requestDTO.getInstrument());
//
//        return AddLightningMeetingParticipantResponseDTO.builder()
//                .lightningMeetingParticipant(participant)
//                .build();
//    }
//
//    public int getMeetingParticipantNum(Long meetingId) {
//        return participantRepository.getMeetingParticipantNum(meetingId);
//    }
//}
