package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.repository.post.mapper.PostMapper;
import pungmul.pungmul.repository.post.repository.PostRepository;

@RequiredArgsConstructor
@Repository
public class MybatisPostRepository implements PostRepository {
    private final PostMapper postMapper;

    @Override
    public void save(Post post) {
        postMapper.save(post);
    }

    @Override
    public void likePost(Long userId, Long postId) {
        postMapper.likePost(userId, postId);
    }

    @Override
    public void plusPostLikeNum(Long postId) {
        postMapper.plusPostLikeCount(postId);
    }

    @Override
    public Integer postLikedNum(Long postId) {
        return postMapper.postLikedNum(postId);
    }

    @Override
    public boolean isPostLikedByUser(Long userId, Long postId) {
        return postMapper.isPostLikedByUser(userId, postId);
    }

    @Override
    public void unlikePost(Long userId, Long postId) {
        postMapper.unlikePost(userId, postId);
    }

    @Override
    public void minusPostLikeNum(Long postId) {
        postMapper.minusPostLikeNum(postId);
    }
}
