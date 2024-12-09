package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Content;

import java.util.Optional;

@Mapper
public interface ContentMapper {
    void save(Content content);

    Optional<Content> getContentByPostId(Long postId);

    void updateContentByPostId(Long id, String text);
}
