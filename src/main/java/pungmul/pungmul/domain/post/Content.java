package pungmul.pungmul.domain.post;

import lombok.*;

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
    private List<Long> imageIdList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
