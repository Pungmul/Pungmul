package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingInvitationMessageDTO {
    private Long meetingId;           // 모임 ID
    private Long invitationId;
    private String meetingName;       // 모임 이름
    private Long founderId;           // 모임 개설자 ID
    private String founderName;       // 모임 개설자 이름
    private String content;           // 초대 메시지 내용
//    private LocalDateTime sentAt;  // 초대 메시지 생성 시간
}
