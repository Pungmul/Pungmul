package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.CommentLikeResponseDTO;
import pungmul.pungmul.dto.post.PostLikeResponseDTO;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.service.post.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{commentId}/like")
    public ResponseEntity<BaseResponse<CommentLikeResponseDTO>> likeCommentByCommentId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestParam String category
    ) {
        CommentLikeResponseDTO commentLikeResponseDTO = commentService.handleCommentLike(userDetails.getAccountId(), commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, commentLikeResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{commentId}")
    public ResponseEntity<BaseResponse<Void>> replyCommentByCommentId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestParam Long postId,
            @RequestBody RequestCommentDTO requestCommentDTO
    ) {
        commentService.addComment(userDetails.getAccountId(), postId, commentId, requestCommentDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }
}
