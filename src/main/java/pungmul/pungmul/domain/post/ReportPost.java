package pungmul.pungmul.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPost {
    private Long id;
    private Long postId;
    private Long userId;
    private ReportReason reportReason;
    private LocalDateTime reportTime;
}
