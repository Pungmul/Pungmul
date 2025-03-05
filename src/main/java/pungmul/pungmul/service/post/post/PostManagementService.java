package pungmul.pungmul.service.post.post;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pungmul.pungmul.service.common.PageHelperService;
import pungmul.pungmul.service.member.membermanagement.UserService;
import pungmul.pungmul.service.post.comment.CommentService;
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
@Slf4j
public class PostManagementService {
    private final PostRepository postRepository;
    private final PostValidationService validationService;
    private final PostContentService contentService;
    private final UserRepository userRepository;
    private final PostLimitService postLimitService;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final PostBuilderService postBuilderService;

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

        if (validationService.isNotAdminUser(userDetails) && isHiddenPost(post)) return postBuilderService.getHiddenPostResponseDTO(post);

        return postBuilderService.getPostResponseDTO(post, content, postLikedByUser);
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
        List<Post> postListByCategory = postRepository.getPostListByCategory(categoryId, isAdmin);

        // DTO 변환
        List<SimplePostDTO> postDTOList = postListByCategory.stream()
                .map(postBuilderService::getSimplePostDTO)
                .collect(Collectors.toList());

        // 페이징 정보 생성 및 반환
        PageInfo<Post> originalPageInfo = new PageInfo<>(postListByCategory);

        return PageHelperService.copyPageInfo(originalPageInfo, postDTOList);
    }

    public SimplePostDTO getHotPost(Long categoryId) {
        return postRepository.getHotPost(categoryId)
                .filter(post -> post.getLikeNum() >= hotPostMinLikeNum)
                .map(postBuilderService::getSimplePostDTO)
                .orElse(null);
    }

    //  추후 구현
    public boolean isHotPost(Long postId) {
        Long categoryId = getPostById(postId).getCategoryId();
        SimplePostDTO hotPost = getHotPost(categoryId);
        return hotPost != null && hotPost.getPostId().equals(postId);
    }

    private Long savePost(Long categoryId, Long userId) throws IOException {
        Post post = postBuilderService.getPost(categoryId);
        postRepository.save(post);

        postLimitService.incrementPostCount(userId);

        return post.getId();
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
        List<Post> originHotPosts = postRepository.getHotPosts(hotPostMinLikeNum);

        List<SimplePostAndCategoryDTO> hotPostsDTO = originHotPosts.stream().map(postBuilderService::getSimplePostAndCategoryDTO).toList();

//        PageInfo<SimplePostAndCategoryDTO> hotPostsPageInfo = new PageInfo<>(hotPostsDTO);

        return GetHotPostsResponseDTO.builder()
                .hotPosts(new PageInfo<>(hotPostsDTO))
                .build();
    }

    public GetUserPostsResponseDTO getUserPosts(UserDetailsImpl userDetails, Integer page, Integer size) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        PageHelper.startPage(page, size);

        List<Post> postsByUserId = postRepository.getPostsByUserId(user.getId());

        List<SimplePostAndCategoryDTO> list = postsByUserId.stream().map(postBuilderService::getSimplePostAndCategoryDTO).toList();

        // 원본 PageInfo의 페이지 정보를 유지한 채 DTO 리스트를 적용

        return GetUserPostsResponseDTO.builder()
                .userPosts(new PageInfo<>(list))
                .build();
    }

    public GetHiddenPostResponseDTO getHiddenPosts(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Post> hiddenPosts = postRepository.getHiddenPosts();

        List<SimplePostAndCategoryDTO> hiddenPostsDTO = hiddenPosts.stream().map(postBuilderService::getSimplePostAndCategoryDTO)
                .toList();

        return GetHiddenPostResponseDTO.builder()
                .hiddenPosts(new PageInfo<>(hiddenPostsDTO))
                .build();
    }
}
