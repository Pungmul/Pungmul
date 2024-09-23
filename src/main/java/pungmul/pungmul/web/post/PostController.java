package pungmul.pungmul.web.post;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.LocalPostResponseDTO;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.post.CreatePostResponseDTO;
import pungmul.pungmul.dto.post.post.PostResponseDTO;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.service.post.PostService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<BaseResponse<CreatePostResponseDTO>> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long categoryId,
            @Validated @RequestPart("postData") PostRequestDTO postRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        CreatePostResponseDTO createPostResponseDTO = postService.addPost(userDetails.getAccountId(), categoryId, postRequestDTO, files);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, createPostResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponseDTO>> getPostByPostId(@PathVariable Long postId) {
        PostResponseDTO post = postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, post));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostLikeResponseDTO>> likePostByPostId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) {
        PostLikeResponseDTO postLikeResponseDTO = postService.handlePostLike(userDetails.getAccountId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, postLikeResponseDTO));
    }
}
