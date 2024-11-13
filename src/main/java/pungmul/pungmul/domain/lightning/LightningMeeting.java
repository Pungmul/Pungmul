package pungmul.pungmul.domain.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeeting {
    private Long id;
    private String meetingName;
    private String meetingDescription;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minPersonNum;
    private Integer maxPersonNum;
    private Long organizerId;
    private MeetingType meetingType;
    private Double latitude;
    private Double longitude;
    private List<InstrumentAssignment> instrumentAssignmentList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
