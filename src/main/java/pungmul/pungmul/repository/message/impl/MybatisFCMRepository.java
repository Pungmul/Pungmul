package pungmul.pungmul.repository.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.dto.message.UpdateFCMTokenDTO;
import pungmul.pungmul.repository.message.mapper.FCMMapper;
import pungmul.pungmul.repository.message.repository.FCMRepository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MybatisFCMRepository implements FCMRepository {
    private final FCMMapper fcmMapper;

    @Override
    public void saveOrUpdateToken(FCMToken fcmToken) {
        // 해당 사용자의 FCMToken이 이미 존재하는지 확인
        List<FCMToken> existingTokens = fcmMapper.selectTokensByUserId(fcmToken.getUserId());

        boolean tokenExists = existingTokens.stream()
                .anyMatch(token -> token.getToken().equals(fcmToken.getToken()));

        if (tokenExists) {
            // 토큰이 이미 존재하면 갱신
            fcmMapper.updateToken(UpdateFCMTokenDTO.builder()
                            .token(fcmToken.getToken())
                            .isValid(fcmToken.isValid())
                            .build());
        } else {
            // 토큰이 없다면 새로 생성
            fcmMapper.insertToken(fcmToken);
        }
    }

    @Override
    public List<FCMToken> findTokensByUserId(Long userId) {
        return fcmMapper.selectTokensByUserId(userId);
    }

    @Override
    public void invalidateToken(String token) {
        fcmMapper.updateTokenValidity(UpdateFCMTokenDTO.builder()
                    .token(token)
                    .isValid(false)
                    .build());
    }

    @Override
    public List<FCMToken> getValidTokens() {
        return fcmMapper.selectValidTokens();
    }

    @Override
    public void updateTokenValidity(UpdateFCMTokenDTO updateFCMTokenDTO) {
        fcmMapper.updateTokenValidity(updateFCMTokenDTO);
    }
}
