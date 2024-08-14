package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.repository.post.repository.CommentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public Long addComment(Long userId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {

        log.info("call addComment service");
        Comment comment = getComment(userId, postId, parentId, requestCommentDTO);

        commentRepository.save(comment);
        log.info("save addComment done : {}", comment.getId());
        return comment.getId();
    }

    private static Comment getComment(Long userId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {
        return Comment.builder()
                .postId(postId)
                .userId(userId)
                .parentId(parentId)
                .content(requestCommentDTO.getContent())
                .build();
    }

    public Long likeComment(Long userId, Long commentId) {
        commentRepository.likeComment(userId, commentId);
        return commentRepository.getCommentLikesNum(commentId);
    }
}
