package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.dto.Mappable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetLightningMeetingParticipantsRequestDTO implements Mappable {
    private List<LatLong> meetingParticipants;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("meetingParticipants", meetingParticipants);
        return map;
    }
}
