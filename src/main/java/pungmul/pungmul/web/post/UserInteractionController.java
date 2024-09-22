package pungmul.pungmul.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.board.BoardPostsResponseDTO;
import pungmul.pungmul.service.post.board.BoardService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class UserInteractionController {



}
