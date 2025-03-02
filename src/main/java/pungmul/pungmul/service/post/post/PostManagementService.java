package pungmul.pungmul.service.post.post;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.*;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.board.GetHotPostsResponseDTO;
import pungmul.pungmul.dto.post.post.*;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CategoryRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.post.CommentService;
import pungmul.pungmul.service.post.TimeSincePosted;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostManagementService {
    private static final Logger log = LoggerFactory.getLogger(PostManagementService.class);
    private final PostRepository postRepository;
    private final PostValidationService validationService;
    private final PostContentService contentService;
    private final UserRepository userRepository;
    private final PostLimitService postLimitService;
    private final TimeSincePosted timeSincePosted;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final CategoryRepository categoryRepository;

    @Value("${post.hot.minLikes}")
    private Integer hotPostMinLikeNum;

    //
    @Transactional
    public CreatePostResponseDTO addPost(UserDetailsImpl userDetails, Long categoryId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException, ExceededPostingNumException, ForbiddenPostingUserException {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        validationService.validateUserForPosting(userId);

        log.info("files : {}", files.isEmpty());
        Long postId = savePost(categoryId, userId);
        contentService.saveContent(userId, postId, postRequestDTO, files);

        return CreatePostResponseDTO.builder().postId(postId).build();
    }

    public Post getPostById(Long postId) {
        return postRepository.getPostById(postId).orElseThrow(NoSuchElementException::new);
    }

    public PostResponseDTO getPostDTOById(UserDetailsImpl userDetails, Long postId) {
        Post post = postRepository.getPostById(postId).orElseThrow(NoSuchElementException::new);
        Content content = contentService.getContentByPostId(postId);
        boolean postLikedByUser = isPostLikedByUser(userDetails, postId);

        if (validationService.isNotAdminUser(userDetails) && isHiddenPost(post)) return getHiddenPostResponseDTO(post);

        return getPostResponseDTO(post, content, postLikedByUser);
    }

    @Transactional
    public UpdatePostResponseDTO updatePost(UserDetailsImpl userDetails, Long postId, UpdatePostRequestDTO updatePostRequestDTO, List<MultipartFile> files) throws IOException {
        validatePostUpdate(userDetails, postId);

        contentService.updateContent(userDetails, postId, updatePostRequestDTO, files);
        return UpdatePostResponseDTO.builder().postId(postId).build();
    }

    private void validatePostUpdate(UserDetailsImpl userDetails, Long postId) {
        if (!validationService.isAuthor(userDetails, postId))
            throw new NotPostAuthorException("자신이 작성한 게시물이 아닙니다.");
        if (isHotPost(postId))
            throw new HotPostModificationException("인기 게시물은 내용을 수정할 수 없습니다.");
    }

    public PageInfo<SimplePostDTO>  getPostsByCategory(Long categoryId, Integer page, Integer size, UserDetails userDetails) {
        // 사용자 권한 확인
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // 페이징 설정
        PageHelper.startPage(page, size);

        // 데이터베이스에서 모든 게시물 조회
        List<Post> postListByCategory = postRepository.getPostListByCategory(categoryId);

        // 관리자 권한이 없는 경우 hidden 또는 deleted가 true인 게시물 제외
        List<Post> filteredPosts = postListByCategory.stream()
                .filter(post -> isAdmin || (!post.getHidden() && !post.getDeleted())) // 조건에 따라 필터링
                .toList();

        // DTO 변환
        List<SimplePostDTO> postDTOList = filteredPosts.stream()
                .map(this::getSimplePostDTO)
                .collect(Collectors.toList());

        // 페이징 정보 생성 및 반환
        return new PageInfo<>(postDTOList);
    }

    public SimplePostDTO getHotPost(Long categoryId) {
        return postRepository.getHotPost(categoryId)
                .filter(post -> post.getLikeNum() >= hotPostMinLikeNum)
                .map(this::getSimplePostDTO)
                .orElse(null);
    }

    //  추후 구현
    public boolean isHotPost(Long postId) {
        Long categoryId = getPostById(postId).getCategoryId();
        SimplePostDTO hotPost = getHotPost(categoryId);
        return hotPost != null && hotPost.getPostId().equals(postId);
    }

    private Long savePost(Long categoryId, Long userId) throws IOException {
        Post post = getPost(categoryId);
        postRepository.save(post);

        postLimitService.incrementPostCount(userId);

        return post.getId();
    }

    private static Post getPost(Long categoryId) {
        return Post.builder()
                .categoryId(categoryId)
                .build();
    }

    private PostResponseDTO getHiddenPostResponseDTO(Post postById){
        return PostResponseDTO.builder()
                .postId(postById.getId())
                .title("삭제된 게시글")
                .content("")
                .author("")
                .build();
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

    private boolean isHiddenPost(Post post) {
        return post.getHidden() || post.getDeleted();
    }

    public boolean isPostLikedByUser(UserDetails userDetails, Long postId) {
        Account account = accountRepository.getAccountByUsername(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);
        Long userId = userRepository.getUserIdByAccountId(account.getId());
        return postRepository.isPostLikedByUser(userId, postId);
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

    public SimplePostAndCategoryDTO getSimplePostAndCategoryDTO(Post post) {
        Content contentByPostId = contentService.getContentByPostId(post.getId());
        return SimplePostAndCategoryDTO.builder()
                .postId(post.getId())
                .title(contentByPostId.getTitle())
                .content(contentByPostId.getText())
                .author(getAuthorNameOrAnonymous(contentByPostId))
                .timeSincePosted(getTimeSincePosted(post.getCreatedAt()))
                .timeSincePostedText(timeSincePosted.getTimeSincePostedText(post.getCreatedAt()))
                .viewCount(post.getViewCount())
                .likedNum(post.getLikeNum())
                .categoryId(post.getCategoryId())
                .categoryName(categoryRepository.getCategoryById(post.getCategoryId()).getName())
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

    public void deletePost(UserDetailsImpl userDetails, Long postId) {
        Long writerId = contentService.getContentByPostId(postId).getWriterId();
        User user = userService.getUserByEmail(userDetails.getUsername());

        log.info("userId : {}, writerId : {}", user.getId(), writerId);

        if (!user.getId().equals(writerId))
            throw new NotValidPostAccessException();
        postRepository.deletePost(postId);
    }

    @Transactional
    public GetHotPostsResponseDTO getHotPosts(Integer page, Integer size) {
        PageHelper.startPage(page, size);

        List<Post> hotPosts = postRepository.getHotPosts(hotPostMinLikeNum);
        List<SimplePostAndCategoryDTO> hotPostsDTO = hotPosts.stream().map(this::getSimplePostAndCategoryDTO).toList();

//        PageInfo<SimplePostAndCategoryDTO> hotPostsPageInfo = new PageInfo<>(hotPostsDTO);

        return GetHotPostsResponseDTO.builder()
                .hotPosts(new PageInfo<>(hotPostsDTO))
                .build();
    }


    public GetUserPostsResponseDTO getUserPosts(UserDetailsImpl userDetails, Integer page, Integer size) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        PageHelper.startPage(page, size);

        List<Post> postsByUserId = postRepository.getPostsByUserId(user.getId());

        List<SimplePostAndCategoryDTO> list = postsByUserId.stream().map(this::getSimplePostAndCategoryDTO).toList();

        // 원본 PageInfo의 페이지 정보를 유지한 채 DTO 리스트를 적용

        return GetUserPostsResponseDTO.builder()
                .userPosts(new PageInfo<>(list))
                .build();
    }
}
