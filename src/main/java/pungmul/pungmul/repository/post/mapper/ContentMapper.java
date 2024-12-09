package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.dto.post.post.ContentUpdateDTO;

import java.util.Optional;

@Mapper
public interface ContentMapper {
    void save(Content content);

    Optional<Content> getContentByPostId(Long postId);

    void updateContentByPostId(ContentUpdateDTO contentUpdateDTO);
}
