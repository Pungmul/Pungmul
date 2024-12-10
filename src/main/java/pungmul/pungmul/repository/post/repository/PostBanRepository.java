package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.PostBan;

import java.util.Optional;

public interface PostBanRepository {
    Optional<PostBan> getActiveBanByUserId(Long userId);

    void deactivateBan(Long banId);
}
