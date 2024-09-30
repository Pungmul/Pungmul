package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.dto.post.CommentResponseDTO;

import java.util.List;

@Mapper
public interface CommentMapper {
    void save(Comment comment);

    void likeComment(Long userId, Long commentId);

    void unlikeComment(Long userId, Long commentId);

    Boolean isCommentLikedByUser(Long userId, Long commentId);

    Integer getCommentLikesNum(Long commentId);

    void plusCommentLikeNum(Long commentId);

    void minusCommentLikeNum(Long commentId);

    Comment getCommentById(Long commentId);

    List<Comment> getCommentsByPostId(Long postId);
}
