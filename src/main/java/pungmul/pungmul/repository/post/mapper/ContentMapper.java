package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Content;

@Mapper
public interface ContentMapper {
    void save(Content content);

    Content getContentByPostId(Long postId);
}
