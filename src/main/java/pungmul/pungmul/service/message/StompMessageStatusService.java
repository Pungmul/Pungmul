package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.message.StompMessageLog;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;
import pungmul.pungmul.repository.message.repository.StompMessageReadStatusRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StompMessageStatusService {
    private final StompMessageReadStatusRepository stompMessageReadStatusRepository;
    private final UserService userService;

    public boolean isMessageRead(Long messageId, Long receiverId){
        return stompMessageReadStatusRepository.isMessageRead(messageId, receiverId);
    }

    @Transactional
    public void markMessagesAsRead(List<Long> messageIdList, String receiverUsername) {
        if (messageIdList == null || messageIdList.isEmpty()) {
            throw new IllegalArgumentException("메시지 ID 목록이 비어 있습니다.");
        }

        Long receiverId = userService.getUserByEmail(receiverUsername).getId();
        stompMessageReadStatusRepository.markMessageAsRead(messageIdList, receiverId);
    }

//    public List<StompMessageLog> getUnreadMessages(Long receiverId){
//        return stompMessageReadStatusRepository.findUnreadMessages(receiverId);
//    }

//    public List<Long> getRecipientUserIds(String domain, String businessIdentifier) {
//        try {
//            return recipientUserRepository.findRecipientsByDomainAndIdentifier(domain, businessIdentifier);
//        } catch (Exception e) {
//            log.error("수신자 목록 조회 실패: {}", e.getMessage());
//            return List.of(); // 조회 실패 시 빈 리스트 반환
//        }
//    }
}
