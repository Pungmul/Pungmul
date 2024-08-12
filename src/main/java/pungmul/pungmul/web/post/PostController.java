package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.service.post.PostService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> addPost(@Validated @RequestPart("postData") RequestPostDTO requestPostDTO,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        return ResponseEntity.ok(postService.addPost(requestPostDTO.getUserId(), requestPostDTO, files));
    }
}
