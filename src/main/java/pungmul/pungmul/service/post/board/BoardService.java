package pungmul.pungmul.service.post.board;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.post.NoMoreDataException;
import pungmul.pungmul.domain.post.board.Category;
import pungmul.pungmul.dto.post.board.BoardDetailsResponseDTO;
import pungmul.pungmul.dto.post.board.BoardInfoDTO;
import pungmul.pungmul.dto.post.board.CategoryDTO;
import pungmul.pungmul.dto.post.post.SimplePostDTO;
import pungmul.pungmul.repository.post.repository.CategoryRepository;
import pungmul.pungmul.service.post.post.PostManagementService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final CategoryRepository categoryRepository;
    private final PostManagementService postManagementService;

    public List<CategoryDTO> getBoardList(Long categoryId) {
        List<Category> categoryList;
        if (categoryId == null)
            categoryList = categoryRepository.getRootCategory();
        else
            categoryList = categoryRepository.getChildCategory(categoryId);

        return getCategoryResponseDTOList(categoryList);
    }

    private static List<CategoryDTO> getCategoryResponseDTOList(List<Category> categoryList) {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryDTOList.add(
                        CategoryDTO.builder()
                            .id(category.getId())
                            .parentId(category.getParentId())
                            .name(category.getName())
                            .description(category.getDescription())
                            .build()
                    );
        }
        log.info("getCategoryResponseDTOList: {}", categoryDTOList);
        return categoryDTOList;
    }


    public BoardDetailsResponseDTO getInitialBoardData(Long categoryId, UserDetailsImpl userDetails) {
        if (!categoryRepository.isCategoryExistById(categoryId))
            throw new NoSuchElementException();

        BoardInfoDTO boardInfo = getBoardInfo(categoryId);
        SimplePostDTO hotPost = postManagementService.getHotPost(categoryId);
        PageInfo<SimplePostDTO> recentPosts = postManagementService.getPostsByCategory(categoryId, 1, 20, userDetails);

        return getBoardDetailsResponseDTO(boardInfo, hotPost, recentPosts);
    }

    @Transactional
    public PageInfo<SimplePostDTO> getAdditionalPosts(Long categoryId, Integer page, Integer size, UserDetailsImpl userDetails) {
        if (!categoryRepository.isCategoryExistById(categoryId))
            throw new NoSuchElementException("해당 카테고리 없음");

        PageInfo<SimplePostDTO> pageInfo = postManagementService.getPostsByCategory(categoryId, page, size, userDetails);

        // 더 이상 호출할 데이터가 없는 경우 예외 처리
        if (page > 1 && (pageInfo.getList().isEmpty() || pageInfo.isIsLastPage())) {
            throw new NoMoreDataException("더 이상 호출할 데이터가 없습니다.");
        }
        return postManagementService.getPostsByCategory(categoryId, page, size, userDetails);
    }

    public BoardDetailsResponseDTO getBoardPosts(Long categoryId, Integer page, Integer size, UserDetailsImpl userDetails) {
        if (!categoryRepository.isCategoryExistById(categoryId))
            throw new NoSuchElementException("해당 카테고리 없음");

        BoardInfoDTO boardInfo = new BoardInfoDTO();
        SimplePostDTO hotPost = new SimplePostDTO();

        if (page == 1){
             boardInfo = getBoardInfo(categoryId);
             hotPost = postManagementService.getHotPost(categoryId);
        }

        PageInfo<SimplePostDTO> pageInfo = postManagementService.getPostsByCategory(categoryId, page, size, userDetails);
        log.info("pageInfo 정보 - 현재 페이지: {}, 총 페이지 수: {}, 전체 데이터 개수: {}, isLastPage: {}",
                pageInfo.getPageNum(), pageInfo.getPages(), pageInfo.getTotal(), pageInfo.isIsLastPage());

//        // 더 이상 호출할 데이터가 없는 경우 예외 처리
//        if (page > 1 && (pageInfo.getList().isEmpty() || pageInfo.isIsLastPage())) {
//            log.warn("NoMoreDataException 발생 - page: {}, size: {}, total: {}", page, size, pageInfo.getTotal());
//            throw new NoMoreDataException("더 이상 호출할 데이터가 없습니다.");
//        }
        return getBoardDetailsResponseDTO(boardInfo, hotPost, pageInfo);
    }



    private BoardDetailsResponseDTO getBoardDetailsResponseDTO(
            BoardInfoDTO boardInfo,
            SimplePostDTO hotPost,
            PageInfo<SimplePostDTO> recentPosts) {
        return BoardDetailsResponseDTO.builder()
                .boardInfo(boardInfo)
                .hotPost(hotPost)
                .recentPostList(recentPosts)
                .build();
    }

    private BoardInfoDTO getBoardInfo(Long categoryId) {
        Category categoryByName = categoryRepository.getCategoryById(categoryId);
        if (!categoryByName.isRootCategory()) {
            Category parentCategory = categoryRepository.getCategoryById(categoryByName.getParentId());
            return getBoardInfoDTO(parentCategory, categoryByName);
        }
        return getBoardInfoDTO(categoryByName);
    }

    private BoardInfoDTO getBoardInfoDTO(Category rootCategory){
        return BoardInfoDTO.builder()
                    .rootCategoryName(rootCategory.getName())
                    .build();
    }

    private BoardInfoDTO getBoardInfoDTO(Category rootCategory, Category childCategory){
        return BoardInfoDTO.builder()
                .rootCategoryName(rootCategory.getName())
                .childCategoryName(childCategory.getName())
                .build();
    }
}


