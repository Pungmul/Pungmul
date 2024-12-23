package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.post.ReportPostResponseDTO;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.repository.post.repository.ReportPostRepository;
import pungmul.pungmul.service.member.membermanagement.AccountService;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostInteractionService {
    private final PostRepository postRepository;
    private final ReportPostRepository reportPostRepository;
    private final PostNotificationService notificationService;
    private final AccountService accountService;
    private final UserService userService;

    @Transactional
    public PostLikeResponseDTO handlePostLike(Long accountId, Long postId) {
        boolean isLiked = postRepository.toggleLike(accountId, postId);
        Integer likeCount = postRepository.getLikeCount(postId);
        if (!isLiked) {
            notificationService.triggerLikeNotification(postId, accountId);
        }
        return PostLikeResponseDTO.builder().postId(postId).liked(isLiked).likedNum(likeCount).build();
    }

    @Transactional
    public ReportPostResponseDTO reportPost(Long accountId, Long postId, String reportReason) {
        reportPostRepository.reportPost(accountId, postId, reportReason);
        Integer reportCount = reportPostRepository.getReportCount(postId);
        if (reportCount >= 5) {
            postRepository.hidePost(postId);
        }
        return new ReportPostResponseDTO(postId, reportReason, reportCount);
    }

    public boolean isPostLikedByUser(UserDetails userDetails, Long postId) {
        Account account = accountService.getAccountByLoginId(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);
        Long userId = userService.getUserIdByAccountId(account.getId());
        return postRepository.isPostLikedByUser(userId, postId);
    }
}
