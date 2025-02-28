package pungmul.pungmul.dto.post.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.post.ReportReason;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportResponseDTO {
    private Long commentId;
    private ReportReason reportReason;
}
