package pungmul.pungmul.domain.post;

import lombok.*;
import pungmul.pungmul.domain.file.Image;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {
    private Long id;
    private Long postId;
    private Long writerId;
    private Boolean anonymity;
    private String title;
    private String text;
    private List<Image> imageList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
