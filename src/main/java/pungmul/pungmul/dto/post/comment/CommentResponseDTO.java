package pungmul.pungmul.dto.post.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private Long postId;
    private Long parentId;
    private String content;
    private String userName;
    private Boolean deleted;
    private Image profile;
    private String createdAt;
}
