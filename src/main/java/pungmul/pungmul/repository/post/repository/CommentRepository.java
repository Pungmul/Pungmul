package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Comment;

public interface CommentRepository {
    void save(Comment comment);

    void likeComment(Long userId, Long commentId);

    Long getCommentLikesNum(Long commentId);
}
