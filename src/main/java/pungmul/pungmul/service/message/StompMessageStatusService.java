package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;
import pungmul.pungmul.repository.message.repository.StompMessageReadStatusRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StompMessageStatusService {
    private final StompMessageReadStatusRepository stompMessageReadStatusRepository;

    public boolean isMessageRead(Long messageId, Long receiverId){
        return stompMessageReadStatusRepository.isMessageRead(messageId, receiverId);
    }

    public void markMessageAsRead(Long messageId, Long receiverId){
        stompMessageReadStatusRepository.markMessageAsRead(messageId, receiverId);
    }

//    public List<StompMessageReadStatus> findUnreadMessages(Long receiverId){
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
