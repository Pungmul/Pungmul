package pungmul.pungmul.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.dto.chat.ChatMessage;
import pungmul.pungmul.repository.chat.repository.ChatRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void saveMessage(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessagesByChatRoomId(Long chatRoomId) {
        return chatRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatMessage> getMessagesByUser(String userId) {
        return chatRepository.findBySenderOrRecipient(userId);
    }
}
