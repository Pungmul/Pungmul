package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteUserToMeetingRequestDTO {
    private List<String> inviteUserEmailList;
    private Long meetingId;
}
