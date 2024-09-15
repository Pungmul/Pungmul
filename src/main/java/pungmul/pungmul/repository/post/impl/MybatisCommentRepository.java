package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.repository.post.mapper.CommentMapper;
import pungmul.pungmul.repository.post.repository.CommentRepository;


@Repository
@RequiredArgsConstructor
public class MybatisCommentRepository implements CommentRepository {
    private final CommentMapper commentMapper;

    @Override
    public void save(Comment comment) {
        commentMapper.save(comment);
    }

    @Override
    public void likeComment(Long userId, Long commentId) {
        commentMapper.likeComment(userId, commentId);
    }

    @Override
    public void unlikeComment(Long userId, Long commentId) {
        commentMapper.unlikeComment(userId, commentId);
    }

    @Override
    public void minusCommentLikeNum(Long commentId) {
        commentMapper.minusCommentLikeNum(commentId);
    }

    @Override
    public void plusCommentLikeNum(Long commentId) {
        commentMapper.plusCommentLikeNum(commentId);
    }

    @Override
    public Integer getCommentLikesNum(Long commentId) {
        return commentMapper.getCommentLikesNum(commentId);
    }

    @Override
    public Boolean isCommentLikedByUser(Long userId, Long commentId) {
        return commentMapper.isCommentLikedByUser(userId, commentId);
    }
}
