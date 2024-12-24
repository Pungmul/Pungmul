package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.core.exception.custom.post.HotPostModificationException;
import pungmul.pungmul.core.exception.custom.post.NotPostAuthorException;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.domain.post.PostBan;
import pungmul.pungmul.domain.post.PostLimit;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.PostBanRepository;
import pungmul.pungmul.repository.post.repository.PostLimitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostValidationService {
    private final UserRepository userRepository;
    private final PostBanRepository postBanRepository;
    private final PostLimitRepository postLimitRepository;
    private final PostContentService postContentService;

    public void validateUserForPosting(Long userId) throws ForbiddenPostingUserException, ExceededPostingNumException {
        if (isForbiddenUser(userId)) {
            throw new ForbiddenPostingUserException("게시물 작성이 제한된 계정입니다.");
        }
        if (isExceededPostingNum(userId)) {
            throw new ExceededPostingNumException("금일 작성 가능 게시물 수를 초과하였습니다.");
        }
    }

//    public void validatePostAccess(UserDetailsImpl userDetails, Post post) throws ForbiddenPostingUserException {
//        if (post.isHidden() && !userDetails.isAdmin()) {
//            throw new ForbiddenPostingUserException("Access denied to hidden post");
//        }
//    }

//    public void validatePostUpdate(UserDetailsImpl userDetails, Long postId) {
//        if (!isAuthor(userDetails, postId))
//            throw new NotPostAuthorException("자신이 작성한 게시물이 아닙니다.");
//        if (isHotPost(postId))
//            throw new HotPostModificationException("인기 게시물은 내용을 수정할 수 없습니다.");
//    }

    //  추후 구현
    public boolean isAuthor(UserDetailsImpl userDetails, Long postId) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new);
        Long writerId = postContentService.getContentByPostId(postId).getWriterId();

        return userId.equals(writerId);
    }

//    //  추후 구현
//    public boolean isHotPost(Long postId) {
//        Long categoryId = postManagementService.getPostById(postId).getCategoryId();
//        SimplePostDTO hotPost = postManagementService.getHotPost(categoryId);
//        return hotPost != null && hotPost.getPostId().equals(postId);
//    }

    private boolean isForbiddenUser(Long userId) {
        Optional<PostBan> activeBan = postBanRepository.getActiveBanByUserId(userId);

        if (activeBan.isPresent()) {
            PostBan ban = activeBan.get();
            // 금지 종료 시간이 현재 시간을 지났다면 금지 해제
            if (ban.getBanEndTime() != null && ban.getBanEndTime().isBefore(LocalDateTime.now())) {
                postBanRepository.deactivateBan(ban.getId());
                return false; // 금지가 해제되었으므로 작성 가능
            }
            return true; // 여전히 금지 상태
        }
        return false; // 금지 상태가 아님
    }

    private boolean isExceededPostingNum(Long userId) {
        PostLimit postLimit = postLimitRepository.findPostLimitByUserId(userId).orElseGet(() -> {
            PostLimit newPostLimit = PostLimit.builder()
                    .userId(userId)
                    .postCount(0)
                    .lastResetTime(LocalDateTime.now())
                    .build();
            postLimitRepository.insertPostLimit(newPostLimit);

            return newPostLimit;
        });

        // 날짜 비교: 초기화 여부 판단
        LocalDate lastResetDate = postLimit.getLastResetTime().toLocalDate();
        LocalDate today = LocalDate.now();

        if (!lastResetDate.isEqual(today)) {
            // 데이터베이스에서 postCount와 lastResetTime 업데이트 (CURRENT_TIMESTAMP 사용)
            postLimitRepository.updatePostLimit(postLimit.getId());
            return false; // 제한을 초과하지 않음
        }

        // 작성 횟수 확인
        return postLimit.getPostCount() >= 5;
    }

    public boolean isHiddenPost(Post post) {
        return post.getHidden() || post.getDeleted();
    }

    public boolean isNotAdminUser(UserDetailsImpl userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return !isAdmin;
    }
}



