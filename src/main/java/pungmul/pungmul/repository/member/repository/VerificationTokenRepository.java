package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.auth.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    void saveVerificationToken(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);
}