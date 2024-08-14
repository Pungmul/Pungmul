package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.dto.post.RequestCommentDTO;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.service.member.loginvalidation.user.User;
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

    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> addPost(@User SessionUser sessionUser,
                                        @Validated @RequestPart("postData") RequestPostDTO requestPostDTO,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        log.info("SessionUser: {}", sessionUser.getUserId());
        return ResponseEntity.ok(postService.addPost(sessionUser.getUserId(), requestPostDTO, files));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> addComment(@User SessionUser sessionUser,
                                           @PathVariable Long postId,
                                           @RequestParam(required = false) Long parentId,
                                           @Validated @RequestBody RequestCommentDTO requestCommentDTO){
        log.info("call addComment");
        Long commentId = commentService.addComment(sessionUser.getUserId(), postId, parentId, requestCommentDTO);

        return ResponseEntity.ok(commentId);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Integer> likePost(@User SessionUser sessionUser,
                                         @PathVariable Long postId){
        return ResponseEntity.ok(postService.likePost(sessionUser.getUserId(), postId));
    }

    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<Long> likeComment(@User SessionUser sessionUser,
//                                            @PathVariable Long postId,
                                            @PathVariable Long commentId){
        return ResponseEntity.ok(commentService.likeComment(sessionUser.getUserId(), commentId));
    }
}
