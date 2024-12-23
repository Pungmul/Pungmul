package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.post.*;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.service.post.TimeSincePosted;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostManagementService {
    private final PostRepository postRepository;
    private final PostValidationService validationService;
    private final PostContentService contentService;
    private final UserRepository userRepository;
    private final PostLimitService postLimitService;
    private final TimeSincePosted timeSincePosted;
    private final PostInteractionService postInteractionService;

    @Value("${post.hot.minLikes}")
    private Integer hotPostMinLikeNum;

    @Value("1")
    private Integer FORBID_REPORT_COUNT_NUM;

    @Transactional
    public CreatePostResponseDTO addPost(UserDetailsImpl userDetails, Long categoryId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException, ExceededPostingNumException, ForbiddenPostingUserException {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new);
        validationService.validateUserForPosting(userId);

        Long postId = savePost(categoryId, userId);
        contentService.saveContent(userId, postId, postRequestDTO, files);

        return CreatePostResponseDTO.builder().postId(postId).build();
    }

    private Long savePost(Long categoryId, Long userId) throws IOException {
        Post post = getPost(categoryId);
        postRepository.save(post);

        postLimitService.incrementPostCount(userId);

        return post.getId();
    }

    public Post getPostById(Long postId) {
        return postRepository.getPostById(postId).orElseThrow(NoSuchElementException::new);
    }

    public PostResponseDTO getPost(UserDetailsImpl userDetails, Long postId) {
        Post post = postRepository.getPostById(postId).orElseThrow(NoSuchElementException::new);
        Content content = contentService.getContentByPostId(postId);
        boolean postLikedByUser = isPostLikedByUser(userDetails, postId);

        if (isNotAdminUser(userDetails) && isHiddenPost(post)) return getHiddenPostResponseDTO(post);

        return getPostResponseDTO(post, content, postLikedByUser);
    }


    @Transactional
    public UpdatePostResponseDTO updatePost(UserDetailsImpl userDetails, Long postId, UpdatePostRequestDTO updatePostRequestDTO, List<MultipartFile> files) throws IOException {
        validationService.validatePostUpdate(userDetails, postId);

        contentService.updateContent(userDetails, postId, updatePostRequestDTO, files);
        return UpdatePostResponseDTO.builder().postId(postId).build();
    }

    public SimplePostDTO getHotPost(Long categoryId) {
        return postRepository.getHotPost(categoryId)
                .filter(post -> post.getLikeNum() >= hotPostMinLikeNum)
                .map(this::getSimplePostDTO)
                .orElse(null);
    }

    public SimplePostDTO getSimplePostDTO(Post post) {
        Content contentByPostId = contentService.getContentByPostId(post.getId());
        return SimplePostDTO.builder()
                .postId(post.getId())
                .title(contentByPostId.getTitle())
                .content(contentByPostId.getText())
                .author(getAuthorNameOrAnonymous(contentByPostId))
                .timeSincePosted(getTimeSincePosted(post.getCreatedAt()))
                .timeSincePostedText(timeSincePosted.getTimeSincePostedText(post.getCreatedAt()))
                .viewCount(post.getViewCount())
                .likedNum(post.getLikeNum())
                .build();
    }

    private String getAuthorNameOrAnonymous(Content content) {
        if (content.getAnonymity())
            return "Anonymous";
        Optional<User> userByUserId = userRepository.getUserByUserId(content.getWriterId());
        return userByUserId.map(User::getName)
                .orElse("Unknown User");
    }

    private Integer getTimeSincePosted(LocalDateTime postedTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(postedTime, now);

        return (int) duration.toMinutes();
    }
}
