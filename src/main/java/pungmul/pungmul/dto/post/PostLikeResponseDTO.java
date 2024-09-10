package pungmul.pungmul.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeResponseDTO {
    private Long postId;
    private Boolean liked;
    private Integer likedNum;
}
