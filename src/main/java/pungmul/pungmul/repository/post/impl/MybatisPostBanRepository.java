package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.PostBan;
import pungmul.pungmul.repository.post.mapper.PostBanMapper;
import pungmul.pungmul.repository.post.repository.PostBanRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisPostBanRepository implements PostBanRepository {
    private final PostBanMapper postBanMapper;

    @Override
    public Optional<PostBan> getActiveBanByUserId(Long userId) {
        return postBanMapper.getActiveBanByUserId();
    }

    @Override
    public void deactivateBan(Long banId) {
        postBanMapper.deactivateBan(banId);
    }
}
