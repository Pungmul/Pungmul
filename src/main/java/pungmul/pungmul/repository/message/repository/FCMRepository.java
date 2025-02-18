package pungmul.pungmul.repository.message.repository;

import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.dto.message.UpdateFCMTokenDTO;

import java.util.List;

public interface FCMRepository {
    void saveOrUpdateToken(FCMToken fcmToken);
    List<FCMToken> findTokensByUserId(Long userId);
    void invalidateToken(String token);
    List<FCMToken> getValidTokens();

    void updateTokenValidity(UpdateFCMTokenDTO updateFCMTokenDTO);

    Long getUserIdByFCMToken(String token);
}
