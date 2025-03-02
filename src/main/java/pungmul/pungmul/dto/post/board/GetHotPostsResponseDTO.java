package pungmul.pungmul.dto.post.board;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.post.post.SimplePostAndCategoryDTO;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHotPostsResponseDTO {
    private PageInfo<SimplePostAndCategoryDTO> hotPosts;
}
