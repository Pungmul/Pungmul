package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Post;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {
    void save(Post post);

    void likePost(Long userId, Long postId);

    void plusPostLikeCount(Long postId);

    Integer postLikedNum(Long postId);

    boolean isPostLikedByUser(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);

    void minusPostLikeNum(Long postId);

    List<Post> getPostListByCategory(String categoryName);

    Optional<Post> getHotPost(String categoryName);

    Post getPostById(Long postId);
}
