package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.lightning.MeetingType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLightningMeetingRequestDTO {
    private String meetingName;
    private String meetingDescription;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minPersonNum;
    private Integer maxPersonNum;
    private MeetingType meetingType;
    private Double latitude;
    private Double longitude;
}
