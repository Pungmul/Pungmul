package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.board.Category;
import pungmul.pungmul.repository.post.mapper.CategoryMapper;
import pungmul.pungmul.repository.post.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
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
    public List<Category> getChildCategory(Long categoryId) {
        log.info("getChildCategory categoryId: {}", categoryId);
        List<Category> childCategoryList = categoryMapper.getChildCategoryList(categoryId);
        log.info("childCategoryList: {}", childCategoryList);
        return childCategoryList;

    }

    @Override
    public boolean isCategoryExistById(Long categoryId) {
        return categoryMapper.isCategoryExistById(categoryId);
    }

    @Override
    public List<Category> getCategoryByName(String categoryName) {
        return categoryMapper.getCategoryByName(categoryName);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryMapper.getCategoryById(id);
    }
}
