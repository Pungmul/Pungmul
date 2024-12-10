package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.post.UpdatePostRequestDTO;
import pungmul.pungmul.repository.post.mapper.PostMapper;
import pungmul.pungmul.repository.post.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisPostRepository implements PostRepository {
    private final PostMapper postMapper;

    @Override
    public List<Post> getPostListByCategory(Long categoryId) {
        return postMapper.getPostListByCategory(categoryId);
    }

    @Override
    public Optional<Post> getHotPost(Long categoryId) {
        log.info("repository categoryName: {}", categoryId);
        Optional<Post> hotPost = postMapper.getHotPost(categoryId);
        log.info("repository hotPost: {}", hotPost);
        return hotPost;
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        return postMapper.getPostById(postId);
    }

    @Override
    public void hidePost(Long postId) {
        postMapper.hidePost(postId);
    }
//
//    @Override
//    public void updatePost(UpdatePostRequestDTO updatePostRequestDTO) {
//
//
//    }

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
