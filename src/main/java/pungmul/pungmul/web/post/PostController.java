package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.service.post.PostService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/add")
    public ResponseEntity<Long> addPost(@ModelAttribute RequestPostDTO requestPostDTO) throws IOException {
        log.info("post controller");
        return ResponseEntity.ok(postService.addPost(requestPostDTO.getUserId(), requestPostDTO));
    }
}
