package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.board.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> getCategoryList();

    List<Category> getRootCategory();

    List<Category> getChildCategory(Long categoryId);

    boolean isCategoryExistById(Long categoryId);

    List<Category> getCategoryByName(String categoryName);

    Category getCategoryById(Long id);
}
