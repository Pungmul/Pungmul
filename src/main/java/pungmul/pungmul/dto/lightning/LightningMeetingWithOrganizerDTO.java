package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.LightningMeeting;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeetingWithOrganizerDTO {
    private LightningMeeting lightningMeeting;
    private String organizerName;
}
