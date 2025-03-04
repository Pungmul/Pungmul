package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.core.exception.custom.post.NoSuchCommentException;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.repository.post.mapper.CommentMapper;
import pungmul.pungmul.repository.post.repository.CommentRepository;

import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisCommentRepository implements CommentRepository {
    private final CommentMapper commentMapper;

//    @Override
//    public Comment save(Comment comment) {
//        Long saved = commentMapper.save(comment);
//        return commentMapper.getCommentById(saved);
//    }

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
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentMapper.getCommentsByPostId(postId);
    }

    @Override
    public Comment getCommentByCommentId(Long id) {
        return commentMapper.getCommentById(id)
                .orElseThrow(NoSuchCommentException::new);
    }

    @Override
    public void hideComment(Long commentId) {
        commentMapper.hideComment(commentId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentMapper.getCommentsByUserId(userId);
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
