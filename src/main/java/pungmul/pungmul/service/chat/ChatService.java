package pungmul.pungmul.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.repository.chat.repository.ChatRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessage saveMessage(ChatMessageRequestDTO chatMessageRequestDTO) {
        return chatRepository.save(getChatMessage(chatMessageRequestDTO));

    }

    private ChatMessage getChatMessage(ChatMessageRequestDTO chatMessageRequestDTO) {
        return ChatMessage.builder()
                .senderUsername(chatMessageRequestDTO.getSenderUsername())
                .receiverUsername(chatMessageRequestDTO.getReceiverUsername())
                .content(chatMessageRequestDTO.getContent())
                .messageType(chatMessageRequestDTO.getMessageType())
                .imageUrl(chatMessageRequestDTO.getImageUrl())
                .chatRoomUUID(chatMessageRequestDTO.getChatRoomUUID())
                .build();
    }

//    public List<ChatMessage> getMessagesByChatRoomId(Long chatRoomId) {
//        return chatRepository.findByChatRoomId(chatRoomId);
//    }

    public List<ChatMessage> getMessagesByUser(String userId) {
        return chatRepository.findBySenderOrRecipient(userId);
    }

    @Transactional
    public CreateChatRoomResponseDTO createPersonalChatRoom(CreateChatRoomRequestDTO createChatRoomRequestDTO) {
        User sender = userRepository.getUserByEmail(createChatRoomRequestDTO.getSenderName())
                .orElseThrow(() -> new NoSuchElementException("Sender not found with email: " + createChatRoomRequestDTO.getSenderName()));

        User receiver = userRepository.getUserByEmail(createChatRoomRequestDTO.getReceiverName())
                .orElseThrow(() -> new NoSuchElementException("Receiver not found with email: " + createChatRoomRequestDTO.getReceiverName()));

        String roomUUID = chatRoomRepository.createPersonalChatRoom(getPersonalChatRoom(sender, receiver));
        chatRoomRepository.addChatRoomMembers(roomUUID, List.of(sender.getId(), receiver.getId()));

        return CreateChatRoomResponseDTO.builder()
                .roomUUID(roomUUID)
                .senderName(sender.getName())
                .receiverName(receiver.getName())
                .build();
    }

    private ChatRoom getPersonalChatRoom(User sender, User receiver){
        return ChatRoom.builder()
                .roomUUID(UUID.randomUUID().toString())
                .createdBy(sender.getEmail())
                .chatRoomMemberIds(List.of(sender.getId(), receiver.getId()))
                .build();
    }
}
