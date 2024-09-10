package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Post;

public interface PostRepository {
    void save(Post post);


    void likePost(Long userId, Long postId);

    void plusPostLikeNum(Long postId);

    Integer postLikedNum(Long postId);

    boolean isPostLikedByUser(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);

    void minusPostLikeNum(Long postId);
}
