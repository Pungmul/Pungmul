package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentUpdateDTO {
    private Long contentId;
    private Boolean anonymity;
    private String text;
}
