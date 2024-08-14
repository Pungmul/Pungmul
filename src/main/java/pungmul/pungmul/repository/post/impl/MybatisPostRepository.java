package pungmul.pungmul.repository.post.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.repository.post.mapper.PostMapper;
import pungmul.pungmul.repository.post.repository.PostRepository;

@Repository
public class MybatisPostRepository implements PostRepository {
    private final PostMapper postMapper;

    public MybatisPostRepository(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public void save(Post post) {
        postMapper.save(post);
    }

    @Override
    public void likePost(Long userId, Long postId) {
        postMapper.likePost(userId, postId);
    }

    @Override
    public void plusPostLikeCount(Long postId) {
        postMapper.plusPostLikeCount(postId);
    }

    @Override
    public Integer postLikedNum(Long postId) {
        return postMapper.postLikedNum(postId);
    }
}
