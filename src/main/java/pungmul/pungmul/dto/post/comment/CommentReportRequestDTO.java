package pungmul.pungmul.dto.post.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.post.ReportReason;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportRequestDTO {
    private ReportReason reportReason;
}
