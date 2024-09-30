package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.auth.VerificationToken;
import pungmul.pungmul.repository.member.mapper.VerificationTokenMapper;
import pungmul.pungmul.repository.member.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MybatisVerificationTokenRepository implements VerificationTokenRepository {
    private final VerificationTokenMapper verificationTokenMapper;
    @Override
    public void saveVerificationToken(VerificationToken token) {
        verificationTokenMapper.saveVerificationToken(token);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenMapper.findByToken(token);
    }

    @Override
    public void deleteByUserId(Long userId) {
        verificationTokenMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        verificationTokenMapper.deleteExpiredTokens(now);
    }
}
