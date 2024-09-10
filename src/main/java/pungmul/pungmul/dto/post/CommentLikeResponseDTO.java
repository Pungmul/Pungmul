package pungmul.pungmul.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeResponseDTO {
    private Long commentId;
    private Boolean liked;
    private Integer likeCount;
}
