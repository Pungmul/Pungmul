package pungmul.pungmul.web.post;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.post.board.BoardDetailsResponseDTO;
import pungmul.pungmul.dto.post.board.CategoryDTO;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.service.post.PostService;
import pungmul.pungmul.service.post.board.BoardService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    /**
     * 최상위 카테고리(부모 카테고리가 없는 게시판) 목록을 가져오는 메서드입니다.
     *
     * 이 메서드는 @PreAuthorize 어노테이션을 통해 'USER' 역할을 가진 사용자만 접근할 수 있습니다.
     * 호출 시, BoardService를 사용하여 부모 카테고리가 없는 최상위 카테고리 목록을 가져오고,
     * 해당 목록을 BaseResponse 객체에 담아 응답으로 반환합니다.
     *
     * @PreAuthorize("hasRole('USER')")는 'USER' 이상의 권한을 가진 인증된 사용자만 이 메서드에 접근할 수 있도록 합니다.
     * @GetMapping("")은 이 메서드를 컨트롤러의 루트 엔드포인트에 GET 요청으로 매핑합니다.
     *
     * @return BaseResponseCode.OK 상태와 함께 CategoryDTO 객체 리스트를 포함한 ResponseEntity를 반환합니다.
     */

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse<List<CategoryDTO>>> getRootBoards(){
        List<CategoryDTO> boardList = boardService.getRootBoardList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, boardList));
    }

//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/{categoryName}")
//    public ResponseEntity<BaseResponse<BoardDetailsResponseDTO>> getInitialBoardData(@PathVariable Long categoryId){
//        BoardDetailsResponseDTO boardDetailsResponseDTO = boardService.getInitialBoardData(categoryId);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, boardDetailsResponseDTO));
//    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{categoryId}")
    public ResponseEntity<BaseResponse<BoardDetailsResponseDTO>> getInitialBoardData(@PathVariable Long categoryId){
        BoardDetailsResponseDTO boardDetailsResponseDTO = boardService.getInitialBoardData(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, boardDetailsResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{categoryId}/add")
    public ResponseEntity<BaseResponse<PageInfo<SimplePostDTO>>> getAdditionalPosts(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "2") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    )
    {
        PageInfo<SimplePostDTO> additionalPosts = boardService.getAdditionalPosts(categoryId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, additionalPosts));
    }


}
