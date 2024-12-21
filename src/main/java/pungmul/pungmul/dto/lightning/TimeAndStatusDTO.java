package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.LightningMeetingStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeAndStatusDTO {
    private LocalDateTime now;
    private LightningMeetingStatus status;
}
