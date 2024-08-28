package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.JwtToken;
import pungmul.pungmul.repository.member.mapper.JwtTokenMapper;
import pungmul.pungmul.repository.member.repository.JwtTokenRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisJwtTokenRepository implements JwtTokenRepository {
    private final JwtTokenMapper jwtTokenMapper;

    @Override
    public Optional<JwtToken> findByToken(String token) {
        return jwtTokenMapper.findByToken(token);
    }

    @Override
    public List<JwtToken> findAllValidTokensByUser(Long accountId) {
        return jwtTokenMapper.findAllValidTokensByUser(accountId);
    }

    @Override
    public void revokeTokensByAccountId(Long accountId) {
        jwtTokenMapper.revokeTokensByAccountId(accountId);
    }

    @Override
    public void revokeTokenByToken(String token) {
        jwtTokenMapper.revokeTokenByToken(token);
    }

    @Override
    public void save(JwtToken token) {
        jwtTokenMapper.save(token);
    }

    @Override
    public void saveAll(List<JwtToken> tokens) {
        jwtTokenMapper.saveAll(tokens);
    }
}
