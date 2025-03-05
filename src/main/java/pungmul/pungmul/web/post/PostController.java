package pungmul.pungmul.web.post;

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
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.post.*;
import pungmul.pungmul.service.post.post.PostInteractionService;
import pungmul.pungmul.service.post.post.PostManagementService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostManagementService postManagementService;
    private final PostInteractionService postInteractionService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<BaseResponse<CreatePostResponseDTO>> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long categoryId,
            @Validated @RequestPart("postData") PostRequestDTO postRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader(value = "Content-Type") String contentType, // 특정 헤더 추출
            @RequestHeader(value = "Authorization") String authorization // 특정 헤더 추출
    ) throws IOException, ExceededPostingNumException, ForbiddenPostingUserException {
        log.info("Content-Type: {}", contentType);
        log.info("Authorization: {}", authorization);

        CreatePostResponseDTO createPostResponseDTO = postManagementService.addPost(userDetails, categoryId, postRequestDTO, files);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, createPostResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{postId}")
    public ResponseEntity<BaseResponse<UpdatePostResponseDTO>> updatePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestPart("postData") UpdatePostRequestDTO updatePostRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        log.info("call updatePost Controller method");
        UpdatePostResponseDTO updatePostResponseDTO = postManagementService.updatePost(userDetails, postId,updatePostRequestDTO, files);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, updatePostResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponseDTO>> getPostByPostId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId) {
        PostResponseDTO post = postManagementService.getPostDTOById(userDetails,postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, post));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostLikeResponseDTO>> likePostByPostId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) {
        PostLikeResponseDTO postLikeResponseDTO = postInteractionService.handlePostLike(userDetails, postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, postLikeResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/report")
    public ResponseEntity<BaseResponse<ReportPostResponseDTO>> reportPostByPostId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestBody ReportPostRequestDTO reportPostRequestDTO
    ) {
        ReportPostResponseDTO reportPostResponseDTO = postInteractionService.reportPostByPostId(userDetails, postId, reportPostRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, reportPostResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePostByPostId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId
    ){
        postManagementService.deletePost(userDetails, postId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<BaseResponse<GetUserPostsResponseDTO>> getUserPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "20", required = false) Integer size
    ){
        log.info("size : {}", size);
        GetUserPostsResponseDTO userPosts = postManagementService.getUserPosts(userDetails, page, size);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, userPosts));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hidden")
    public ResponseEntity<BaseResponse<GetHiddenPostResponseDTO>> getHiddenPosts(
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "20", required = false) Integer size
    ){
        GetHiddenPostResponseDTO hiddenPosts = postManagementService.getHiddenPosts(page, size);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, hiddenPosts));
    }
}
