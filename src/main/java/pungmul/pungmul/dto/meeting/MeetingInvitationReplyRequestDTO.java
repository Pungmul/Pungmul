package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.meeting.InvitationStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingInvitationReplyRequestDTO {
    private Long meetingId;
    private Long invitationId;
    private InvitationStatus invitationStatus; // ACCEPTED, DECLINED, DEFERRED
}
