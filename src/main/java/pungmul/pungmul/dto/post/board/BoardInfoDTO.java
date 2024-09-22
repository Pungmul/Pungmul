package pungmul.pungmul.dto.post.board;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardInfoDTO {
    private String rootCategoryName;
    private String childCategoryName;
}
