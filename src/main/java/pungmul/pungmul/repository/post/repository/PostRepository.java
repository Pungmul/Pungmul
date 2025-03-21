package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.post.GetHiddenPostResponseDTO;
import pungmul.pungmul.dto.post.post.UpdatePostRequestDTO;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    void save(Post post);


    void likePost(Long userId, Long postId);

    void plusPostLikeNum(Long postId);

    Integer postLikedNum(Long postId);

    boolean isPostLikedByUser(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);

    void minusPostLikeNum(Long postId);

    List<Post> getPostListByCategory(Long categoryId, Boolean isAdmin);

    Optional<Post> getHotPost(Long categoryId);

    Optional<Post> getPostById(Long postId);

    void hidePost(Long postId);

    void deletePost(Long postId);

    List<Post> getHotPosts(Integer thresholdLikes);

    List<Post> getPostsByUserId(Long userId);

    List<Post> getHiddenPosts();

//    void updatePost(UpdatePostRequestDTO updatePostRequestDTO);
}
