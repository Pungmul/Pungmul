package pungmul.pungmul.dto.post.post;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHiddenPostResponseDTO {
    private PageInfo<SimplePostAndCategoryDTO> hiddenPosts;
}
