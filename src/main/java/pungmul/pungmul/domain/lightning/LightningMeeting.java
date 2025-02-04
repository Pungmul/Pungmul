package pungmul.pungmul.domain.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightningMeeting {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
