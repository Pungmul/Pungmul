package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.meeting.MeetingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeetingResponseDTO {
    private Long meetingId;
    private String meetingName;
    private String meetingDescription;
    private Boolean isPublic;
    private MeetingStatus meetingStatus;
    private String founderUserName;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
