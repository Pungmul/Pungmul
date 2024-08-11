package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Post;

@Mapper
public interface PostMapper {
    void save(Post post);
}
