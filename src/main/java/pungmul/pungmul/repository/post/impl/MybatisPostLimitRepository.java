package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.PostLimit;
import pungmul.pungmul.repository.post.mapper.PostLimitMapper;
import pungmul.pungmul.repository.post.repository.PostLimitRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MybatisPostLimitRepository implements PostLimitRepository {
    private final PostLimitMapper postLimitMapper;

    @Override
    public Optional<PostLimit> findPostLimitByUserId(Long userId) {
        return postLimitMapper.findPostLimitByUserId(userId);
    }

    @Override
    public void insertPostLimit(PostLimit postLimit) {
        postLimitMapper.insertPostLimit(postLimit);
    }

    @Override
    public void incrementPostCount(Long userId) {
        postLimitMapper.incrementPostCount(userId);
    }

    @Override
    public void resetAllPostCount() {
        postLimitMapper.resetAllPostCount();
    }

    @Override
    public void updatePostLimit(Long id) {
        postLimitMapper.updatePostLimit(id);
    }
}
