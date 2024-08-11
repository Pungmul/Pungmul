package pungmul.pungmul.domain.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    private Long id;
    private Long categoryId;
    private Integer viewCount;
    private Integer likeNum;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
