package pungmul.pungmul.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;

import java.time.LocalDateTime;

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
    private Image profile;
    private String createdAt;
}
