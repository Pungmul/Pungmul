package pungmul.pungmul.dto.post.post;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserPostsResponseDTO {
    private PageInfo<SimplePostAndCategoryDTO> userPosts;
}
