package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.Comment;

@Mapper
public interface CommentMapper {
    void save(Comment comment);

    void likeComment(Long userId, Long commentId);

    Long getCommentLikesNum(Long commentId);
}
