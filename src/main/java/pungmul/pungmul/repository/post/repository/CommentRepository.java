package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Comment;

public interface CommentRepository {
    Comment save(Comment comment);

    void likeComment(Long userId, Long commentId);

    Integer getCommentLikesNum(Long commentId);

    Boolean isCommentLikedByUser(Long userId, Long commentId);

    void unlikeComment(Long userId, Long commentId);

    void minusCommentLikeNum(Long commentId);

    void plusCommentLikeNum(Long commentId);
}
