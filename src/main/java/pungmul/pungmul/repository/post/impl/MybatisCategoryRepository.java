package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.board.Category;
import pungmul.pungmul.repository.post.mapper.CategoryMapper;
import pungmul.pungmul.repository.post.repository.CategoryRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisCategoryRepository implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getCategoryList() {
        return categoryMapper.getCategoryList();
    }

    @Override
    public List<Category> getRootCategory() {
        return categoryMapper.getRootCategoryList();
    }

    @Override
    public boolean isCategoryExist(String categoryName) {
        return categoryMapper.isCategoryExist(categoryName);
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        return categoryMapper.getCategoryByName(categoryName);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryMapper.getCategoryById(id);
    }
}
