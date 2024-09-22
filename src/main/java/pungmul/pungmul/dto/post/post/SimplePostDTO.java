package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SimplePostDTO {
    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likedNum;
    private Integer timeSincePosted;
    private String timeSincePostedText;
    private String author;
}
