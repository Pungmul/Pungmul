package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.post.ReportReason;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostResponseDTO {
    private Long postId;
    private String postName;
    private ReportReason reportReason;
    private LocalDateTime reportTime;
}
