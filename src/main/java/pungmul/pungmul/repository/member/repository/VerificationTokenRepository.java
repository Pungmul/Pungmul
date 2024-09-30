package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.auth.VerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository {
    void saveVerificationToken(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);

    void deleteExpiredTokens(LocalDateTime now);
}