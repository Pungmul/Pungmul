package pungmul.pungmul.service.post;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.domain.post.PostLimit;
import pungmul.pungmul.domain.post.ReportPost;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.LocalPostResponseDTO;
import pungmul.pungmul.dto.post.post.*;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.*;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final TimeSincePosted timeSincePosted;
    private final CommentService commentService;
    private final AccountRepository accountRepository;
    private final ReportPostRepository reportPostRepository;
    private final PostLimitRepository postLimitRepository;

    @Value("${post.hot.minLikes}")
    private Integer hotPostMinLikeNum;

    public PostResponseDTO getPostById(UserDetails userDetails, Long postId) {
        Post post = postRepository.getPostById(postId);
        Content content = getContentByPostId(postId);
        boolean postLikedByUser = isPostLikedByUser(userDetails, postId);

        return getPostResponseDTO(post, content, postLikedByUser);
    }

    public Content getContentByPostId(Long postId) {
        Content contentByPostId = contentRepository.getContentByPostId(postId);
        List<Image> imagesByDomainId = imageService.getImagesByDomainId(DomainType.CONTENT, contentByPostId.getId());
        log.info("images by domainId: {}", imagesByDomainId);
        contentByPostId.setImageList(imagesByDomainId);

        return contentByPostId;
    }

    @Transactional
    public CreatePostResponseDTO addPost(UserDetailsImpl userDetails, Long categoryId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException, ExceededPostingNumException, ForbiddenPostingUserException {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new);
        isPostingAllowed(userId);

        Long postId = savePost(categoryId, userId);
        saveContent(userId, postId, postRequestDTO, files);

        return CreatePostResponseDTO.builder()
                .postId(postId)
                .build();
    }

    private void isPostingAllowed(Long userId) throws ExceededPostingNumException, ForbiddenPostingUserException {
        if (isForbiddenUser(userId))
            throw new ForbiddenPostingUserException("게시물 작성이 제한된 계정입니다.");
        if (isExceededPostingNum(userId))
            throw new ExceededPostingNumException("금일 작성 가능 게시물 수를 초과하였습니다.");
    }

    private boolean isForbiddenUser(Long userId) {
        //  추후 정지 계정 정책 구현
        return false;
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

    @Scheduled(cron = "0 0 5 * * ?") // 매일 새벽 5시에 실행
    @Transactional
    public void resetPostLimits() {
        postLimitRepository.resetAllPostCount();
        System.out.println("Post limits reset at " + LocalDateTime.now());
    }

    @Transactional
    public PostLikeResponseDTO handlePostLike(Long accountId, Long postId) {
        Long userId = userRepository.getUserIdByAccountId(accountId);

        // 1. 사용자가 이미 해당 게시물에 좋아요를 눌렀는지 확인
        boolean isLiked = postRepository.isPostLikedByUser(userId, postId);

        if (isLiked) {
            // 2. 이미 좋아요가 눌려 있으면 좋아요 취소 (데이터 삭제) 및 좋아요 수 감소
            postRepository.unlikePost(userId, postId); // 좋아요 취소
            postRepository.minusPostLikeNum(postId); // 좋아요 수 감소
        } else {
            // 3. 좋아요가 눌려 있지 않으면 좋아요 추가 (데이터 삽입) 및 좋아요 수 증가
            postRepository.likePost(userId, postId);   // 좋아요 추가
            postRepository.plusPostLikeNum(postId);  // 좋아요 수 증가
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

    private Long savePost(Long categoryId, Long userId) throws IOException {
        Post post = getPost(categoryId);
        postRepository.save(post);

        postLimitRepository.incrementPostCount(userId);

        return post.getId();
    }

    private void saveContent(Long userId, Long postId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException {
        Content content = getContent(userId, postId, postRequestDTO);
        contentRepository.save(content);

        if (files.isEmpty())
            saveImageList(userId, content.getId(),files);
    }

    private void saveImageList(Long userId, Long contentId, List<MultipartFile> images) throws IOException {
        for (MultipartFile image : images)
            saveContentImage(contentId, image, userId);
    }

    private static Post getPost(Long categoryId) {
        return Post.builder()
                .categoryId(categoryId)
                .build();
    }

    private static Content getContent(Long userId, Long postId, PostRequestDTO postRequestDTO) {
        return Content.builder()
                .postId(postId)
                .title(postRequestDTO.getTitle())
                .text(postRequestDTO.getText())
                .anonymity(postRequestDTO.isAnonymity())
                .writerId(userId)
                .build();
    }

    private boolean isPostLikedByUser(UserDetails userDetails, Long postId) {
        Account account = accountRepository.getAccountByLoginId(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);
        Long userId = userRepository.getUserIdByAccountId(account.getId());
        return postRepository.isPostLikedByUser(userId, postId);
    }

    private void saveContentImage(Long contentId, MultipartFile image, Long userId) throws IOException {
        imageService.saveImage(getRequestContentImageDTO(contentId, image, userId));
    }

    private RequestImageDTO getRequestContentImageDTO(Long contentId, MultipartFile image,  Long userId) {
        return RequestImageDTO.builder()
                .domainId(contentId)
                .imageFile(image)
                .userId(userId)
                .domainType(DomainType.CONTENT)
                .build();
    }

    public SimplePostDTO getHotPost(Long categoryId) {
        return postRepository.getHotPost(categoryId)
                .filter(post -> post.getLikeNum() >= hotPostMinLikeNum)
                .map(this::getSimplePostDTO)
                .orElse(null);
    }

    public SimplePostDTO getSimplePostDTO(Post post) {
        Content contentByPostId = contentRepository.getContentByPostId(post.getId());
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

    private Integer getTimeSincePosted(LocalDateTime postedTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(postedTime, now);

        return (int) duration.toMinutes();
    }

    private String getAuthorNameOrAnonymous(Content content) {
        if (content.getAnonymity())
            return "Anonymous";
        Optional<User> userByUserId = userRepository.getUserByUserId(content.getWriterId());
        return userByUserId.map(User::getName)
                    .orElse("Unknown User");
    }

    public PageInfo<SimplePostDTO> getPostsByCategory(Long categoryId, Integer page, Integer size) {
        PageHelper.startPage(page, size);

        List<Post> postListByCategory = postRepository.getPostListByCategory(categoryId);
        List<SimplePostDTO> postDTOList = new ArrayList<>();
        for (Post post : postListByCategory) {
            postDTOList.add(getSimplePostDTO(post));
        }
        return new PageInfo<>(postDTOList);
    }

    private PostResponseDTO getPostResponseDTO(Post postById, Content contentByPostId, Boolean postLikedByUser) {
        return PostResponseDTO.builder()
                .postId(postById.getId())
                .title(contentByPostId.getTitle())
                .content(contentByPostId.getText())
                .author(getAuthorNameOrAnonymous(contentByPostId))
                .imageList(contentByPostId.getImageList())
                .commentList(commentService.getCommentsByPostId(postById.getId()))
                .timeSincePosted(getTimeSincePosted(postById.getCreatedAt()))
                .timeSincePostedText(timeSincePosted.getTimeSincePostedText(postById.getCreatedAt()))
                .isLiked(postLikedByUser)
                .likedNum(postById.getLikeNum())
                .viewCount(postById.getViewCount())
                .build();
    }

    @Transactional
    public ReportPostResponseDTO reportPostByPostId(UserDetailsImpl userDetails, Long postId, ReportPostRequestDTO reportPostRequestDTO) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        ReportPost reportPost = ReportPost.builder()
                .postId(postId)
                .userId(userId)
                .reportReason(reportPostRequestDTO.getReportReason())
                .build();

        reportPostRepository.reportPost(reportPost);

        return ReportPostResponseDTO.builder()
                .postId(postId)
                .postName(contentRepository.getContentByPostId(postId).getTitle())
                .reportReason(reportPost.getReportReason())
                .reportTime(reportPostRepository.getReportPost(reportPost.getId()).getReportTime())
                .build();
    }
}
