package pungmul.pungmul.dto.post.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardInfoDTO {
    private String rootCategoryName;
    private String childCategoryName;
}
