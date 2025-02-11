package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.dto.lightning.CreateLightningMeetingRequestDTO;
import pungmul.pungmul.dto.lightning.InstrumentAssignRequestDTO;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightningMeetingInstrumentManager {
    private final LightningMeetingInstrumentAssignmentRepository instrumentAssignmentRepository;

    public void createInstrumentAssign(CreateLightningMeetingRequestDTO requestDTO, Long meetingId) {
        for (InstrumentAssignRequestDTO instrumentDTO : requestDTO.getInstrumentAssignRequestDTOList()) {
            instrumentAssignmentRepository.createAssignment(
                    InstrumentAssignment.builder()
                            .meetingId(meetingId)
                            .instrument(instrumentDTO.getInstrument())
                            .minParticipants(instrumentDTO.getMinPersonNum())
                            .maxParticipants(instrumentDTO.getMaxPersonNum())
                            .build()
            );
        }
    }

    public void increaseAssignment(Long meetingId, Instrument assignedInstrument){
        instrumentAssignmentRepository.increaseAssignment(meetingId, assignedInstrument);
    }
}
