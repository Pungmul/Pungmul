package pungmul.pungmul.service.post;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.NotValidCommentAccess;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.domain.post.CommentReport;
import pungmul.pungmul.dto.post.comment.*;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CommentRepository;
import pungmul.pungmul.repository.post.repository.ReportCommentRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.post.post.PostNotificationTrigger;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TimeSincePosted timeSincePosted;
    private final ImageService imageService;
    private final PostNotificationTrigger postNotificationTrigger;
    private final UserService userService;
    private final ReportCommentRepository reportCommentRepository;

    private static final int FORBID_REPORT_COUNT_NUM = 1;

    public CommentResponseDTO addComment(UserDetailsImpl userDetails, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();

        log.info(requestCommentDTO.toString());
        Comment comment = getCommentDTO(userId, postId, parentId, requestCommentDTO);
        commentRepository.save(comment);

        // 댓글인지 대댓글인지 확인 후 알림 트리거 호출
        if (parentId == null) {
            // 댓글 알림
            postNotificationTrigger.triggerCommentNotification(postId, userId, comment.getContent());
        } else {
            // 대댓글(답글) 알림
            postNotificationTrigger.triggerReplyNotification(parentId, userId, comment.getContent());
        }

        return getCommentResponseDTO(commentRepository.getCommentByCommentId(comment.getId()));
    }

    @Transactional
    public CommentLikeResponseDTO handleCommentLike(Long accountId, Long commentId) {
        User user = userRepository.getUserByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);

        Boolean isLiked = commentRepository.isCommentLikedByUser(user.getId(), commentId);

        if (isLiked) {
            commentRepository.unlikeComment(userRepository.getUserIdByAccountId(accountId), commentId);
            commentRepository.minusCommentLikeNum(commentId);
        } else {
            commentRepository.likeComment(userRepository.getUserIdByAccountId(accountId), commentId);
            commentRepository.plusCommentLikeNum(commentId);
        }

        Integer commentLikesNum = commentRepository.getCommentLikesNum(commentId);

        return getCommentLikeResponseDTO(commentId, commentLikesNum, !isLiked);
    }

    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        List<Comment> commentsByPostId = commentRepository.getCommentsByPostId(postId);
        return commentsByPostId.stream()
                .map(this::getCommentResponseDTO)
                .collect(Collectors.toList());
    }

    private Long getUserIdByAccountId(Long accountId) {
        User user = userRepository.getUserByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);

        return user.getId();
    }

    private static Comment getCommentDTO(Long userId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {
        return Comment.builder()
                .postId(postId)
                .userId(userId)
                .parentId(parentId)
                .content(requestCommentDTO.getContent())
                .build();
    }
    public Comment getCommentById(Long commentId){
        return commentRepository.getCommentByCommentId(commentId);
    }

    public CommentResponseDTO getCommentResponseDTO(Comment comment) {
        log.info("comment content : {}", comment.getContent());
        log.info("comment id : {}", comment.getId());

        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .parentId(comment.getParentId())
                .content(comment.getDeleted() ? "삭제된 댓글입니다." : comment.getContent()) // 삭제된 댓글 표시
                .deleted(comment.getDeleted())
                .userName(comment.getDeleted() ? null : userRepository.getUserByUserId(comment.getUserId()).map(User::getName).orElseThrow(NoSuchElementException::new))
                .profile(comment.getDeleted() ? null :imageService.getImagesByDomainId(DomainType.PROFILE, comment.getUserId()).get(0))
                .createdAt(timeSincePosted.getTimePostedText(comment.getCreatedAt()))
                .build();
    }

    private static CommentLikeResponseDTO getCommentLikeResponseDTO(Long commentId, Integer commentLikesNum, Boolean isLiked) {
        return CommentLikeResponseDTO.builder()
                .commentId(commentId)
                .liked(isLiked)
                .likeCount(commentLikesNum)
                .build();
    }

    @Transactional
    public CommentReportResponseDTO reportComment(UserDetailsImpl userDetails, Long commentId, CommentReportRequestDTO requestCommentDTO) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();

        CommentReport commentReport = CommentReport.builder()
                .commentId(commentId)
                .userId(userId)
                .reportReason(requestCommentDTO.getReportReason())
                .build();

        reportCommentRepository.reportComment(commentReport);

        if (reportCommentRepository.getReportCountByCommentId(commentId) >= FORBID_REPORT_COUNT_NUM)
            commentRepository.hideComment(commentId);

        return CommentReportResponseDTO.builder()
                .commentId(commentId)
                .reportReason(commentReport.getReportReason())
                .build();

    }

    @Transactional
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Comment comment = getCommentById(commentId);
        if (!comment.getUserId().equals(user.getId())) {
            throw new NotValidCommentAccess();
        }
        commentRepository.hideComment(commentId);
    }

    public GetUserCommentsResponseDTO getCommentsByUser(UserDetailsImpl userDetails, Integer page, Integer size) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        PageHelper.startPage(page, size);
        List<Comment> commentsByUserId = commentRepository.getCommentsByUserId(user.getId());

        return GetUserCommentsResponseDTO.builder()
                .comments(new PageInfo<>(commentsByUserId))
                .build();
    }
}
