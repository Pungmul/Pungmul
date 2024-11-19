package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLightningMeetingResponseDTO {
    private Long lightningMeetingId;
    private String lightningMeetingName;
    private String organizerName;

}
