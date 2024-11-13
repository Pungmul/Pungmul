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
public class InstrumentAssignment {
    private Long id; // 기본 키
    private Long meetingId; // LightningMeeting과 연결
    private Instrument instrument; // 악기 이름 (예: 쇠, 장구, 북 등)
    private Integer maxParticipants; // 해당 악기 최대 인원
    private Integer currentParticipants; // 현재 참여한 인원
}