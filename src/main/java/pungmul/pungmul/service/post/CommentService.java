package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CommentRepository;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    public Long addComment(Long accountId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {

        log.info("call addComment service");
        Comment comment = getComment(getUserIdByAccountId(accountId), postId, parentId, requestCommentDTO);

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

    public Long likeComment(Long accountId, Long commentId) {
        commentRepository.likeComment(userRepository.getUserIdByAccountId(accountId), commentId);
        return commentRepository.getCommentLikesNum(commentId);
    }

    private Long getUserIdByAccountId(Long accountId) {
        User user = userRepository.getUserByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);

        return user.getId();
    }
}
