package pungmul.pungmul.dto.lightning;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelLightningMeetingRequestDTO {
    private Long meetingId;
    private String newOrganizerUsername;
}
