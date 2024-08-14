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
    public Long getCommentLikesNum(Long commentId) {
        return commentMapper.getCommentLikesNum(commentId);
    }
}
