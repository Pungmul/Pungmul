package pungmul.pungmul.dto.lightning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.dto.Mappable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetNearLightningMeetingResponseDTO implements Mappable {
    private List<LightningMeetingWithOrganizerDTO> lightningMeetingList;

    // Map으로 변환
    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 모듈 등록
        Map<String, Object> resultMap = new HashMap<>();

        // lightningMeetingList 내부 객체들도 직렬화
        List<Map<String, Object>> serializedList = new ArrayList<>();
        for (LightningMeetingWithOrganizerDTO dto : lightningMeetingList) {
            serializedList.add(dto.toMap());
        }
        resultMap.put("lightningMeetingList", serializedList);

        return resultMap;
    }
}
