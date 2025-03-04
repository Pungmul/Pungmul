package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    void save(Comment comment);

    void likeComment(Long userId, Long commentId);

    Integer getCommentLikesNum(Long commentId);

    Boolean isCommentLikedByUser(Long userId, Long commentId);

    void unlikeComment(Long userId, Long commentId);

    void minusCommentLikeNum(Long commentId);

    void plusCommentLikeNum(Long commentId);

    List<Comment> getCommentsByPostId(Long postId);

    Comment getCommentByCommentId(Long id);

    void hideComment(Long commentId);

    List<Comment> getCommentsByUserId(Long userId);
}
