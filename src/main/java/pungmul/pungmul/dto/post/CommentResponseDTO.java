package pungmul.pungmul.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long userId;
    private LocalDateTime createdAt;
}
