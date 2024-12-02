package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.PostLimit;

import java.util.Optional;

@Mapper
public interface PostLimitMapper {
    Optional<PostLimit> findPostLimitByUserId(Long userId);

    void insertPostLimit(PostLimit postLimit);

    void incrementPostCount(Long userId);

    void resetAllPostCount();

    void updatePostLimit(Long postLimitId);
}
