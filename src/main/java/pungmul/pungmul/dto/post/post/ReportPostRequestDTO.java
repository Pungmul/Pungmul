package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.post.ReportReason;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostRequestDTO {
    private ReportReason reportReason;
}
