package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.LightningMeetingStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetMeetingStatusDTO {
    private Long meetingId;
    private LightningMeetingStatus status;
}
