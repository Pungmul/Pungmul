package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.service.post.CommentService;
import pungmul.pungmul.service.post.PostService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> addPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @Validated @RequestPart("postData") RequestPostDTO requestPostDTO,
                                                     @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

//        log.info("SessionUser: {}", sessionUser.getUserId());
        return ResponseEntity.ok(postService.addPost(userDetails.getAccountId(), requestPostDTO, files));
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable Long postId,
                                           @RequestParam(required = false) Long parentId,
                                           @Validated @RequestBody RequestCommentDTO requestCommentDTO){
        log.info("call addComment");
        Long commentId = commentService.addComment(userDetails.getAccountId(), postId, parentId, requestCommentDTO);

        return ResponseEntity.ok(commentId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Integer> likePost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable Long postId){
        return ResponseEntity.ok(postService.likePost(userDetails.getAccountId(), postId));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<Long> likeComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long commentId){
        return ResponseEntity.ok(commentService.likeComment(userDetails.getAccountId(), commentId));
    }
}
