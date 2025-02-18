package pungmul.pungmul.repository.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.message.FCMToken;
import pungmul.pungmul.dto.message.UpdateFCMTokenDTO;

import java.util.List;

@Mapper
public interface FCMMapper {
    void insertToken(FCMToken fcmToken);
    void updateToken(UpdateFCMTokenDTO updateFCMTokenDTO);
    List<FCMToken> selectTokensByUserId(Long userId);
    List<FCMToken> selectValidTokens();

    void updateTokenValidity(UpdateFCMTokenDTO updateFCMTokenDTO);

    Long getUserIdByFCMToken(String token);
}
