package pungmul.pungmul.domain.lightning;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.dto.Mappable;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeetingParticipant implements Mappable {
    private Long id; // 기본 키
    private Long meetingId; // LightningMeeting과의 연결
    private Long userId; // 참여자의 사용자 ID
    private String username; // 사용자 아이디
    private Instrument instrumentAssigned; // 배정된 악기 (없으면 null)
    private boolean organizer; // 모임 주최자 여부
    private LatLong location;
    private Status status;

    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);  // ObjectMapper로 직렬화
    }
}
