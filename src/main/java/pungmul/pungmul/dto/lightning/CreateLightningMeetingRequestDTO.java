package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.lightning.MeetingType;
import pungmul.pungmul.domain.member.instrument.Instrument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLightningMeetingRequestDTO {
    private String meetingName;
    private String meetingDescription;
    private LocalDateTime recruitmentEndTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minPersonNum;
    private Integer maxPersonNum;
    private MeetingType meetingType;
    private List<InstrumentAssignRequestDTO> instrumentAssignRequestDTOList;
    private Instrument organizerInstrument;
    private Double latitude;
    private Double longitude;
}
