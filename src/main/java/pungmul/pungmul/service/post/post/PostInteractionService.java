package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.post.ReportPost;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.post.ReportPostRequestDTO;
import pungmul.pungmul.dto.post.post.ReportPostResponseDTO;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.repository.post.repository.ReportPostRepository;
import pungmul.pungmul.service.member.membermanagement.AccountService;
import pungmul.pungmul.service.member.membermanagement.UserService;

@Service
@RequiredArgsConstructor
public class PostInteractionService {
    private final PostRepository postRepository;
    private final ReportPostRepository reportPostRepository;
    private final PostNotificationService notificationService;
    private final AccountService accountService;
    private final UserService userService;
    private final PostNotificationTrigger postNotificationTrigger;
    private final PostContentService postContentService;

    @Value("1")
    private Integer FORBID_REPORT_COUNT_NUM;

    @Transactional
    public PostLikeResponseDTO handlePostLike(UserDetailsImpl userDetails, Long postId) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();

        // 1. 사용자가 이미 해당 게시물에 좋아요를 눌렀는지 확인
        boolean isLiked = isPostLikedByUser(userId, postId);

        if (isLiked) {
            // 2. 이미 좋아요가 눌려 있으면 좋아요 취소 (데이터 삭제) 및 좋아요 수 감소
            postRepository.unlikePost(userId, postId); // 좋아요 취소
            postRepository.minusPostLikeNum(postId); // 좋아요 수 감소
        } else {
            // 3. 좋아요가 눌려 있지 않으면 좋아요 추가 (데이터 삽입) 및 좋아요 수 증가
            postRepository.likePost(userId, postId);   // 좋아요 추가
            postRepository.plusPostLikeNum(postId);  // 좋아요 수 증가

            //  좋아요 알림 트리거
            postNotificationTrigger.triggerLikeNotification(postId, userId);
        }

        // 4. 게시물의 최신 좋아요 수를 가져옴
        Integer likedNum = postRepository.postLikedNum(postId);

        // 5. 결과 반환 (좋아요 상태 반영)
        return getPostLikeResponseDTO(postId, !isLiked, likedNum);
    }

    private static PostLikeResponseDTO getPostLikeResponseDTO(Long postId, boolean isLiked, Integer likedNum) {
        return PostLikeResponseDTO.builder()
                .postId(postId)
                .liked(isLiked)
                .likedNum(likedNum)
                .build();
    }

    @Transactional
    public ReportPostResponseDTO reportPostByPostId(UserDetailsImpl userDetails, Long postId, ReportPostRequestDTO reportPostRequestDTO) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();

        ReportPost reportPost = ReportPost.builder()
                .postId(postId)
                .userId(userId)
                .reportReason(reportPostRequestDTO.getReportReason())
                .build();

        reportPostRepository.reportPost(reportPost);

        if (reportPostRepository.getReportCountByPostId(postId) >= FORBID_REPORT_COUNT_NUM)
            postRepository.hidePost(postId);

        return ReportPostResponseDTO.builder()
                .postId(postId)
                .postName(postContentService.getContentByPostId(postId).getTitle())
                .reportReason(reportPost.getReportReason())
                .reportTime(reportPostRepository.getReportPost(reportPost.getId()).getReportTime())
                .build();
    }

    public boolean isPostLikedByUser(Long userId, Long postId) {
//        Account account = accountService.getAccountByEmail(userDetails.getUsername());
//        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return postRepository.isPostLikedByUser(userId, postId);
    }
}
