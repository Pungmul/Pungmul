package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.instrument.Instrument;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddLightningMeetingParticipantRequestDTO {
    private Long meetingId;
    private Instrument instrument;
    private Double latitude;
    private Double longitude;
}
