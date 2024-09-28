package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.auth.VerificationToken;

import java.util.Optional;

@Mapper
public interface VerificationTokenMapper {
    void saveVerificationToken(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
