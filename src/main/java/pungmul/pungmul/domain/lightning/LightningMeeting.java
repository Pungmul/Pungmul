package pungmul.pungmul.domain.lightning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.Mappable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeeting implements Mappable {
    private Long id;
    private String meetingName;
//    private String meetingDescription;
    private LocalDateTime recruitmentEndTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minPersonNum;
    private Integer maxPersonNum;
    private Long organizerId;
    private MeetingType meetingType;
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private List<LightningMeetingParticipant> lightningMeetingParticipantList = new ArrayList<>();
    @Builder.Default
    private List<InstrumentAssignment> instrumentAssignmentList = new ArrayList<>();
    private LightningMeetingStatus status;
    private Boolean notificationSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 모듈 등록
        return objectMapper.convertValue(this, Map.class);  // ObjectMapper로 직렬화
    }
}
