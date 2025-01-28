//package pungmul.pungmul.service.lightning;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LightningMeetingInstrumentManager {
//    private final LightningMeetingInstrumentAssignmentRepository instrumentAssignmentRepository;
//
//    public void createInstrumentAssign(CreateLightningMeetingRequestDTO requestDTO, Long meetingId) {
//        for (InstrumentAssignRequestDTO instrumentDTO : requestDTO.getInstrumentAssignRequestDTOList()) {
//            instrumentAssignmentRepository.createAssignment(
//                    InstrumentAssignment.builder()
//                            .meetingId(meetingId)
//                            .instrument(instrumentDTO.getInstrument())
//                            .minParticipants(instrumentDTO.getMinPersonNum())
//                            .maxParticipants(instrumentDTO.getMaxPersonNum())
//                            .build()
//            );
//        }
//    }
//}
