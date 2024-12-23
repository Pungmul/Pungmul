package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.core.exception.custom.post.HotPostModificationException;
import pungmul.pungmul.core.exception.custom.post.NotPostAuthorException;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.PostBanRepository;
import pungmul.pungmul.repository.post.repository.PostLimitRepository;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PostValidationService {
    private final UserRepository userRepository;
    private final PostBanRepository postBanRepository;
    private final PostLimitRepository postLimitRepository;
    private final PostContentService postContentService;
    private final PostManagementService postManagementService;

    public void validateUserForPosting(Long userId) throws ForbiddenPostingUserException, ExceededPostingNumException {
        if (isUserBanned(userId)) {
            throw new ForbiddenPostingUserException("게시물 작성이 제한된 계정입니다.");
        }
        if (isPostingLimitExceeded(userId)) {
            throw new ExceededPostingNumException("금일 작성 가능 게시물 수를 초과하였습니다.");
        }
    }

    public void validatePostAccess(UserDetailsImpl userDetails, Post post) throws ForbiddenPostingUserException {
        if (post.isHidden() && !userDetails.isAdmin()) {
            throw new ForbiddenPostingUserException("Access denied to hidden post");
        }
    }

    public void validatePostUpdate(UserDetailsImpl userDetails, Long postId) {
        if (!isAuthor(userDetails, postId))
            throw new NotPostAuthorException("자신이 작성한 게시물이 아닙니다.");
        if (isHotPost(postId))
            throw new HotPostModificationException("인기 게시물은 내용을 수정할 수 없습니다.");
    }

    //  추후 구현
    private boolean isAuthor(UserDetailsImpl userDetails, Long postId) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new);
        Long writerId = postContentService.getContentByPostId(postId).getWriterId();

        return userId.equals(writerId);
    }

    //  추후 구현
    private boolean isHotPost(Long postId) {
        Long categoryId = postManagementService.getPostById(postId).getCategoryId();
        SimplePostDTO hotPost = postManagementService.getHotPost(categoryId);
        return hotPost != null && hotPost.getPostId().equals(postId);
    }

    private boolean isUserBanned(Long userId) {
        return postBanRepository.isUserBanned(userId);
    }

    private boolean isPostingLimitExceeded(Long userId) {
        return postLimitRepository.isPostingLimitExceeded(userId);
    }
}
