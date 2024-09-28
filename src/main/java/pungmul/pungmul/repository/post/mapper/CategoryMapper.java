package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.board.Category;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    List<Category> getCategoryList();

    List<Category> getRootCategoryList();

    Boolean isCategoryExistById(Long categoryId);

    List<Category> getCategoryByName(String categoryName);

    Category getCategoryById(Long id);

    List<Category> getChildCategoryList(Long categoryId);
}
