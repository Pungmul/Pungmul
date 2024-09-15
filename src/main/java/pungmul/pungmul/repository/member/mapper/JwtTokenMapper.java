package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.auth.JwtToken;

import java.util.List;
import java.util.Optional;

@Mapper
public interface JwtTokenMapper {
    // 특정 사용자의 모든 유효한 토큰 조회
    List<JwtToken> findAllValidTokensByUser(@Param("accountId") Long accountId);

    void revokeTokensByAccountId(Long accountId);

    // 토큰 저장
    void save(JwtToken token);

    // 여러 개의 토큰을 저장 (갱신)
    void saveAll(@Param("tokens") List<JwtToken> tokens);

    Optional<JwtToken> findByToken(String token);

    void revokeTokenByToken(String token);
}
