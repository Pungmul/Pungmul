package pungmul.pungmul.service.post;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.LocalPostResponseDTO;
import pungmul.pungmul.dto.post.post.CreatePostResponseDTO;
import pungmul.pungmul.dto.post.post.PostResponseDTO;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CategoryRepository;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Value("${post.hot.minLikes}")
    private Integer hotPostMinLikeNum;

    public PostResponseDTO getPostById(Long postId) {
        Post post = postRepository.getPostById(postId);
        Content content = getContentByPostId(postId);

        return getPostResponseDTO(post, content);
    }

    public Content getContentByPostId(Long postId) {
        Content contentByPostId = contentRepository.getContentByPostId(postId);
        List<Image> imagesByDomainId = imageService.getImagesByDomainId(DomainType.CONTENT, contentByPostId.getId());
        log.info("images by domainId: {}", imagesByDomainId);
        contentByPostId.setImageList(imagesByDomainId);

        return contentByPostId;
    }

    @Transactional
    public CreatePostResponseDTO addPost(Long accountId, Long categoryId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException {
        Long postId = savePost(categoryId);
        saveContent(userRepository.getUserIdByAccountId(accountId), postId, postRequestDTO, files);

        return CreatePostResponseDTO.builder()
                .postId(postId)
                .build();
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

    private Long savePost(Long categoryId) throws IOException {
        Post post = getPost(categoryId);
        postRepository.save(post);

        return post.getId();
    }

    private void saveContent(Long userId, Long postId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException {
        Content content = getContent(userId, postId, postRequestDTO);
        contentRepository.save(content);

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

    private PostResponseDTO getPostResponseDTO(Post postById, Content contentByPostId) {
        log.info("{}",contentByPostId.getImageList());
        return PostResponseDTO.builder()
                .postId(postById.getId())
                .title(contentByPostId.getTitle())
                .content(contentByPostId.getText())
                .author(getAuthorNameOrAnonymous(contentByPostId))
                .imageList(contentByPostId.getImageList())
                .timeSincePosted(getTimeSincePosted(postById.getCreatedAt()))
                .timeSincePostedText(timeSincePosted.getTimeSincePostedText(postById.getCreatedAt()))
                .likedNum(postById.getLikeNum())
                .viewCount(postById.getViewCount())
                .build();
    }
}
