package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimplePostAndCategoryDTO {
    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likedNum;
    private Integer timeSincePosted;
    private String timeSincePostedText;
    private Long categoryId;
    private String categoryName;
    private String author;
}
