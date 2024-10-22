package pungmul.pungmul.domain.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingInvitation {
    private Long id;
    private Long meetingId;
    private Long founderId;
    private Long receiverId;
    private InvitationStatus invitationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
