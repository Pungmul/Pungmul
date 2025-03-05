package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.post.post.PostResponseDTO;
import pungmul.pungmul.dto.post.post.SimplePostAndCategoryDTO;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.CategoryRepository;
import pungmul.pungmul.service.post.TimeSincePosted;
import pungmul.pungmul.service.post.comment.CommentService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostBuilderService {
    private final PostContentService contentService;
    private final UserRepository userRepository;
    private final TimeSincePosted timeSincePosted;
    private final CategoryRepository categoryRepository;
    private final CommentService commentService;

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

    public PostResponseDTO getPostResponseDTO(Post postById, Content contentByPostId, Boolean postLikedByUser) {
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

    public String getAuthorNameOrAnonymous(Content content) {
        if (content.getAnonymity())
            return "Anonymous";
        Optional<User> userByUserId = userRepository.getUserByUserId(content.getWriterId());
        return userByUserId.map(User::getName)
                .orElse("Unknown User");
    }

    public Integer getTimeSincePosted(LocalDateTime postedTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(postedTime, now);

        return (int) duration.toMinutes();
    }

    public PostResponseDTO getHiddenPostResponseDTO(Post postById){
        return PostResponseDTO.builder()
                .postId(postById.getId())
                .title("삭제된 게시글")
                .content("")
                .author("")
                .build();
    }

    public Post getPost(Long categoryId) {
        return Post.builder()
                .categoryId(categoryId)
                .build();
    }
}
