package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.meeting.InvitationStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingInvitationReplyResponseDTO {
    private Long meetingId;
    private InvitationStatus invitationStatus;
    private String message; // 응답 후의 상태 메시지
}
