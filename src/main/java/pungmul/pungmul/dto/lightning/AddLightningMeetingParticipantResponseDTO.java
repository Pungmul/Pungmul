package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddLightningMeetingParticipantResponseDTO {
    private LightningMeetingParticipant lightningMeetingParticipant;
}
