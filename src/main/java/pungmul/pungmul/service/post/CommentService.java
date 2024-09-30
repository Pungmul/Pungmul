package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Comment;
import pungmul.pungmul.dto.post.CommentLikeResponseDTO;
import pungmul.pungmul.dto.post.CommentResponseDTO;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CommentRepository;
import pungmul.pungmul.service.file.ImageService;

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

    public CommentResponseDTO addComment(Long accountId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {

        Comment comment = getComment(getUserIdByAccountId(accountId), postId, parentId, requestCommentDTO);
        Comment savedComment = commentRepository.save(comment);
//        log.info("comment UserId : {}, comment createdAt : {}", comment.getUserId(), comment.getCreatedAt());

        return getCommentResponseDTO(savedComment);
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

    private static Comment getComment(Long userId, Long postId, Long parentId, RequestCommentDTO requestCommentDTO) {
        return Comment.builder()
                .postId(postId)
                .userId(userId)
                .parentId(parentId)
                .content(requestCommentDTO.getContent())
                .build();
    }

    public CommentResponseDTO getCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .userName(userRepository.getUserByUserId(comment.getUserId()).map(User::getName).orElseThrow(NoSuchElementException::new))
                .profile(imageService.getImagesByDomainId(DomainType.PROFILE, comment.getUserId()).get(0))
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
}
