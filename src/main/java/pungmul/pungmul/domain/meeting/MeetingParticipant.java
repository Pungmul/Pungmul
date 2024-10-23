package pungmul.pungmul.domain.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingParticipant {
    private Long id; // 고유 ID
    private Long meetingId; // 참가하는 모임 ID
    private Long userId; // 참가자 ID
    private LocalDate joinedAt; // 참가한 날짜 및 시간
    private Boolean isHost; // 모임 주최 여부
    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일
}

