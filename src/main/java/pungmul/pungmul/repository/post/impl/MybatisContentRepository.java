package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.repository.post.mapper.ContentMapper;
import pungmul.pungmul.repository.post.repository.ContentRepository;

@RequiredArgsConstructor
@Repository
public class MybatisContentRepository implements ContentRepository {
    private final ContentMapper contentMapper;

    @Override
    public void save(Content content) {
        contentMapper.save(content);
    }
}
