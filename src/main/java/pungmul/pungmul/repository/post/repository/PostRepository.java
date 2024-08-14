package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Post;

public interface PostRepository {
    void save(Post post);


    void likePost(Long userId, Long postId);

    void plusPostLikeCount(Long postId);

    Integer postLikedNum(Long postId);
}
