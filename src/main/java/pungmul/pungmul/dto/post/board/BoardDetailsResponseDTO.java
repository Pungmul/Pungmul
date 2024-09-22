package pungmul.pungmul.dto.post.board;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pungmul.pungmul.dto.post.post.SimplePostDTO;

import java.util.Optional;

@Getter
@Builder
public class BoardDetailsResponseDTO {
    private BoardInfoDTO boardInfo;
    private SimplePostDTO hotPost;
    private PageInfo<SimplePostDTO> recentPostList;
}
