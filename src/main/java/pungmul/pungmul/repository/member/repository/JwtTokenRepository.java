package pungmul.pungmul.repository.member.repository;

import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.JwtToken;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository {
    Optional<JwtToken> findByToken(String token);

    List<JwtToken> findAllValidTokensByUser(Long accountId);

    void revokeTokensByAccountId(Long accountId);

    void revokeTokenByToken(String token);

    // 토큰 저장
    void save(JwtToken token);

    // 여러 개의 토큰을 저장 (갱신)
    void saveAll(List<JwtToken> tokens);


}
