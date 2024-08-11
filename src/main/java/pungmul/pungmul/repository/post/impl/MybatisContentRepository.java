package pungmul.pungmul.repository.post.impl;

import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.repository.post.mapper.ContentMapper;
import pungmul.pungmul.repository.post.repository.ContentRepository;

public class MybatisContentRepository implements ContentRepository {
    private final ContentMapper contentMapper;

    public MybatisContentRepository(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }
    @Override
    public void save(Content content) {
        contentMapper.save(content);
    }
}
