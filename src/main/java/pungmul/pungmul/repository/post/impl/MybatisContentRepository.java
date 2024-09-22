package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.repository.post.mapper.ContentMapper;
import pungmul.pungmul.repository.post.repository.ContentRepository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisContentRepository implements ContentRepository {
    private final ContentMapper contentMapper;

    @Override
    public void save(Content content) {
        contentMapper.save(content);
    }

    @Override
    public Content getContentByPostId(Long postId) {
        return contentMapper.getContentByPostId(postId);
    }
}
