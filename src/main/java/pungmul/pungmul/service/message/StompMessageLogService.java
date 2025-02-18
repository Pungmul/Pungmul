package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessageLog;
import pungmul.pungmul.repository.message.repository.StompMessageLogRepository;
import pungmul.pungmul.repository.message.repository.StompMessageReadStatusRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StompMessageLogService {
    private final StompMessageLogRepository stompMessageLogRepository;
    private final StompMessageReadStatusRepository stompMessageReadStatusRepository;

    /**
     * STOMP 메시지 로그 저장
     * @param senderId 메시지를 보낸 사용자 ID (nullable)
     * @param domainType 메시지 도메인
     * @param businessIdentifier 비즈니스 식별자 (예: 채팅방 ID, 미팅 ID)
     * @param identifier 추가 식별자 (선택적, 예: 사용자 ID, 메시지 ID)
     * @param stompDest 메시지 전송 STOMP 경로
     * @param content 메시지 내용
     */
    @Transactional
    public void logStompMessage(Long senderId, MessageDomainType domainType, String businessIdentifier,
                                String identifier, String stompDest, String content) {
        try {
            StompMessageLog stompMessageLog = StompMessageLog.builder()
                    .senderId(senderId)
                    .domainType(domainType)
                    .businessIdentifier(businessIdentifier)
                    .identifier(identifier)
                    .stompDest(stompDest)
                    .content(content)
                    .sentAt(LocalDateTime.now())
                    .build();

            stompMessageLogRepository.insertStompMessageLog(stompMessageLog);
            log.info("STOMP 메시지 로그 저장: {}", stompMessageLog);
        } catch (Exception e) {
            log.error("STOMP 메시지 로그 저장 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void logStompMessageAndRecipients(Long senderId, MessageDomainType domainType,
                                             String businessIdentifier, String identifier,
                                             String stompDest, String content, List<Long> recipientUserIds) {
        try {
            // 1️⃣ 메시지 저장
            StompMessageLog stompMessageLog = StompMessageLog.builder()
                    .senderId(senderId)
                    .domainType(domainType)
                    .businessIdentifier(businessIdentifier)
                    .identifier(identifier)
                    .stompDest(stompDest)
                    .content(content)
                    .sentAt(LocalDateTime.now())
                    .build();
            stompMessageLogRepository.insertStompMessageLog(stompMessageLog);

            // 2️⃣ 메시지 수신 대상 사용자 기록 (읽음 여부 초기화)
            for (Long recipientUserId : recipientUserIds) {
                stompMessageReadStatusRepository.insertReadStatus(stompMessageLog.getId(), recipientUserId);
            }

            log.info("STOMP 메시지 저장 및 {}명의 수신자 등록", recipientUserIds.size());
        } catch (Exception e) {
            log.error("STOMP 메시지 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * 특정 도메인과 비즈니스 식별자를 기반으로 메시지 로그 조회
     * @param domainType 메시지 도메인
     * @param businessIdentifier 비즈니스 식별자
     * @return 해당 도메인의 메시지 로그 목록
     */
    public List<StompMessageLog> getLogsByDomainAndBusinessIdentifier(MessageDomainType domainType, String businessIdentifier) {
        return stompMessageLogRepository.findByDomainAndBusinessIdentifier(domainType, businessIdentifier);
    }

    /**
     * 특정 도메인의 모든 메시지 로그 조회
     * @param domainType 메시지 도메인
     * @return 해당 도메인의 메시지 로그 목록
     */
    public List<StompMessageLog> getLogsByDomain(MessageDomainType domainType) {
        return stompMessageLogRepository.findByDomainType(domainType);
    }

    /**
     * 전체 메시지 로그 조회
     * @return 모든 STOMP 메시지 로그 목록
     */
    public List<StompMessageLog> getAllLogs() {
        return stompMessageLogRepository.findAll();
    }

    /**
     * 특정 메시지 로그 삭제
     * @param logId 로그 ID
     */
    @Transactional
    public void deleteStompLogById(Long logId) {
        stompMessageLogRepository.deleteById(logId);
    }

    public List<StompMessageLog> findUnreadMessages(Long receiverId){
        return stompMessageLogRepository.findUnreadMessages(receiverId);
    }
}
