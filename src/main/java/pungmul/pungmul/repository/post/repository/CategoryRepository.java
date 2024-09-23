package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.board.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getCategoryList();

    List<Category> getRootCategory();

    boolean isCategoryExistById(Long categoryId);

    Category getCategoryByName(String categoryName);

    Category getCategoryById(Long id);
}
