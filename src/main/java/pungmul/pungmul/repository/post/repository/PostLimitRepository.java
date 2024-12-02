package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.PostLimit;

import java.util.Optional;

public interface PostLimitRepository {
    Optional<PostLimit> findPostLimitByUserId(Long userId);

    void insertPostLimit(PostLimit postLimit);

    void incrementPostCount(Long userId);

    void resetAllPostCount();

    void updatePostLimit(Long id);
}
