package pungmul.pungmul.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.chat.repository.ChatRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ImageService imageService;

    @Transactional
    public CreateChatRoomResponseDTO createChatRoomWithRoomCheck(String senderName, String receiverName){
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findChatRoomByUsers(senderName, receiverName);
        String chatRoomUUID;

        if (existChatRoom.isPresent()){
            chatRoomUUID = existChatRoom.get().getRoomUUID();
        } else {
            chatRoomUUID = createPersonalChatRoom(senderName, receiverName);
        }

        return getCreateChatRoomResponseDTO(chatRoomUUID, senderName, receiverName);
    }

    @Transactional
    public ChatMessage saveMessage(String senderName, String chatRoomUUID, ChatMessageRequestDTO chatMessageRequestDTO) {
        log.info("sender name: {}, chat room uuid: {}", senderName, chatRoomUUID);
        return chatRepository.save(getChatMessage(senderName, chatRoomUUID, chatMessageRequestDTO));
    }

    public String createPersonalChatRoom(String senderName, String receiverName) {
        User sender = userRepository.getUserByEmail(senderName)
                .orElseThrow(() -> new NoSuchElementException("Sender not found with email: " + senderName));
        log.info("Creating personal chat room for user: " + senderName);

        User receiver = userRepository.getUserByEmail(receiverName)
                .orElseThrow(() -> new NoSuchElementException("Receiver not found with email: " + receiverName));
        log.info("Creating personal chat room for user: " + receiverName);

        String roomUUID = chatRoomRepository.createPersonalChatRoom(getPersonalChatRoom(sender, receiver));
        chatRoomRepository.addChatRoomMembers(roomUUID, List.of(sender.getId(), receiver.getId()));

        return roomUUID;
    }
    private void saveChatImage(Long chatId, MultipartFile image, Long userId) throws IOException {
        imageService.saveImage(getRequestChatImageDTO(chatId, image, userId ));
    }

    private RequestImageDTO getRequestChatImageDTO(Long chatId, MultipartFile image, Long userId){
        return RequestImageDTO.builder()
                .domainId(chatId)
                .imageFile(image)
                .userId(userId)
                .domainType(DomainType.CHAT)
                .build();
    }

    private ChatMessage getChatMessage(String senderName, String chatRoomUUID, ChatMessageRequestDTO chatMessageRequestDTO) {
        return ChatMessage.builder()
                .senderUsername(senderName)
                .receiverUsername(chatMessageRequestDTO.getReceiverUsername())
                .content(chatMessageRequestDTO.getContent())
                .chatType(chatMessageRequestDTO.getChatType())
                .imageUrl(chatMessageRequestDTO.getImageUrl())
                .chatRoomUUID(chatRoomUUID)
                .build();
    }

    private static CreateChatRoomResponseDTO getCreateChatRoomResponseDTO(String roomUUID, String senderName, String receiverName) {
        return CreateChatRoomResponseDTO.builder()
                .roomUUID(roomUUID)
                .senderName(senderName)
                .receiverName(receiverName)
                .build();
    }

    private ChatRoom getPersonalChatRoom(User sender, User receiver){
        return ChatRoom.builder()
                .roomUUID(UUID.randomUUID().toString())
                .createdBy(sender.getEmail())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
//                .chatRoomMemberIds(List.of(sender.getId(), receiver.getId()))
                .build();
    }

    public String extractChatRoomUUIDFromDestination(String destination) {
        String[] parts = destination.split("/");
        return parts[parts.length - 1];  // 마지막 부분이 chatRoomUUID
    }
}
