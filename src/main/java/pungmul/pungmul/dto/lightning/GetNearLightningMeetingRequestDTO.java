package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetNearLightningMeetingRequestDTO {
    private Double latitude;
    private Double longitude;
    private Integer mapLevel;
}
