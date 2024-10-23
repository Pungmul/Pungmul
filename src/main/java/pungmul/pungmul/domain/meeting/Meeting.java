package pungmul.pungmul.domain.meeting;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meeting {

    private Long id;

    @NotEmpty @Size(min = 2, max = 20)
    private String meetingName;

    @Size(max = 100)
    private String meetingDescription;

    private Boolean isPublic;

    private MeetingStatus meetingStatus;

    @NotNull
    private Long founderUserId;

    private Integer memberNum;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
