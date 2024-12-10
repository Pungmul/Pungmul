package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.PostBan;

import java.util.Optional;

@Mapper
public interface PostBanMapper {
    Optional<PostBan> getActiveBanByUserId();

    void deactivateBan(Long banId);
}
