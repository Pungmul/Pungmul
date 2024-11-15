package pungmul.pungmul.domain.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.instrument.Instrument;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeetingParticipant {
    private Long id; // 기본 키
    private Long meetingId; // LightningMeeting과의 연결
    private Long userId; // 참여자의 사용자 ID
    private String username; // 사용자 아이디
    private Instrument instrumentAssigned; // 배정된 악기 (없으면 null)
//    private boolean confirmed; // 참가 확정 여부
    private boolean organizer; // 모임 주최자 여부
}
