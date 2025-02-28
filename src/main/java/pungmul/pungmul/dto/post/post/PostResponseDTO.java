package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.dto.post.comment.CommentResponseDTO;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private Long postId;
    private String title;
    private String content;
    private List<Image> imageList;
    private List<CommentResponseDTO> commentList;
    private Integer viewCount;
    private Boolean isLiked;
    private Integer likedNum;
    private Integer timeSincePosted;
    private String timeSincePostedText;
    private String author;
}
