package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.board.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getCategoryList();

    List<Category> getRootCategoryList();

    Boolean isCategoryExist(String category);

    Category getCategoryByName(String categoryName);

    Category getCategoryById(Long id);
}
