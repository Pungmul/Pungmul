package pungmul.pungmul.web.post;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.comment.*;
import pungmul.pungmul.service.post.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<BaseResponse<CommentResponseDTO>> addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam Long postId,
                                                                       @RequestParam(required = false) Long parentId,
                                                                       @Validated @RequestBody RequestCommentDTO requestCommentDTO){
        CommentResponseDTO commentResponseDTO = commentService.addComment(userDetails, postId, parentId, requestCommentDTO);

        log.info("Comment added: {}", commentResponseDTO.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, commentResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{commentId}/like")
    public ResponseEntity<BaseResponse<CommentLikeResponseDTO>> likeCommentByCommentId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        CommentLikeResponseDTO commentLikeResponseDTO = commentService.handleCommentLike(userDetails.getAccountId(), commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, commentLikeResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{commentId}")
    public ResponseEntity<BaseResponse<CommentResponseDTO>> replyCommentByCommentId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestParam Long postId,
            @RequestBody RequestCommentDTO requestCommentDTO
    ) {
        CommentResponseDTO commentResponseDTO = commentService.addComment(userDetails, postId, commentId, requestCommentDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, commentResponseDTO));
    }

    @Secured("hasRole('USER')")
    @PostMapping("/{commentId}/report")
    public ResponseEntity<BaseResponse<CommentReportResponseDTO>> reportComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody CommentReportRequestDTO requestCommentDTO
    ){
        CommentReportResponseDTO commentReportResponseDTO = commentService.reportComment(userDetails, commentId, requestCommentDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, commentReportResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<BaseResponse<Void>> deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("commentId") Long commentId
    ){
        commentService.deleteComment(userDetails, commentId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }
}
