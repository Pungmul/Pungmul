package pungmul.pungmul.web.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.*;
import pungmul.pungmul.service.post.CommentService;
import pungmul.pungmul.service.post.PostService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class LocalPostController {
    private final PostService postService;
    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BaseResponse<LocalPostResponseDTO>> addPost(
                                                    HttpServletRequest request,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @Validated @RequestPart("postData") PostRequestDTO postRequestDTO,
                                                     @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        log.info(request.getHeader("Authorization"));
        LocalPostResponseDTO localPostResponseDTO = postService.addPost(userDetails.getAccountId(), postRequestDTO, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, localPostResponseDTO));
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<BaseResponse<CommentResponseDTO>> addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @PathVariable Long postId,
                                                                       @RequestParam(required = false) Long parentId,
                                                                       @Validated @RequestBody RequestCommentDTO requestCommentDTO){
        CommentResponseDTO commentResponseDTO = commentService.addComment(userDetails.getAccountId(), postId, parentId, requestCommentDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, commentResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostLikeResponseDTO>> likePost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PathVariable Long postId){
        PostLikeResponseDTO postLikeResponseDTO = postService.handlePostLike(userDetails.getAccountId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(BaseResponse.ofSuccess(BaseResponseCode.OK, postLikeResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<BaseResponse<CommentLikeResponseDTO>> likeComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long commentId){

        CommentLikeResponseDTO commentLikeResponseDTO = commentService.handleCommentLike(userDetails.getAccountId(), commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, commentLikeResponseDTO));
    }
}
