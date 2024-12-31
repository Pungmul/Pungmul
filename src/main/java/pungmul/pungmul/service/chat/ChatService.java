package pungmul.pungmul.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.chat.ChatRoom;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.ChatRoomDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;
import pungmul.pungmul.repository.chat.repository.ChatRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ImageService imageService;
    private final UserService userService;

    @Transactional
    public CreateChatRoomResponseDTO createChatRoomWithRoomCheck(String senderName, String receiverName) {
        // 1. 기존 채팅방 존재 여부 확인
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findChatRoomByUsers(senderName, receiverName);

        // 2. 채팅방 UUID 처리
        String chatRoomUUID;
        if (existChatRoom.isPresent()) {
            chatRoomUUID = existChatRoom.get().getRoomUUID();
        } else {
            chatRoomUUID = createPersonalChatRoom(senderName, receiverName);
        }

        // 3. 생성된 채팅방 정보 반환
        return getCreateChatRoomResponseDTO(chatRoomUUID, senderName, receiverName);
    }

    public String createPersonalChatRoom(String senderName, String receiverName) {
        // 1. 채팅방 생성
        String roomUUID = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .roomUUID(roomUUID)
                .createdBy(userService.getUserByEmail(senderName).getId()) // 생성자 ID
                .isGroup(false) // 개인 DM 방이므로 false
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.createPersonalChatRoom(chatRoom);

        // 2. 채팅방 멤버 추가
        Long senderId = userService.getUserByEmail(senderName).getId();
        Long receiverId = userService.getUserByEmail(receiverName).getId();
        chatRoomRepository.addChatRoomMembers(roomUUID, List.of(senderId, receiverId));

        return roomUUID;
    }

    private CreateChatRoomResponseDTO getCreateChatRoomResponseDTO(String chatRoomUUID, String senderName, String receiverName) {
        return CreateChatRoomResponseDTO.builder()
                .roomUUID(chatRoomUUID)
                .senderName(senderName)
                .receiverName(receiverName)
                .build();
    }

    @Transactional
    public ChatMessage saveMessage(String senderName, String chatRoomUUID, ChatMessageRequestDTO chatMessageRequestDTO) {
        log.info("sender name: {}, chat room uuid: {}", senderName, chatRoomUUID);
        return chatRepository.save(getChatMessage(senderName, chatRoomUUID, chatMessageRequestDTO));
    }

//    public String createPersonalChatRoom(String senderName, String receiverName) {
//        User sender = userRepository.getUserByEmail(senderName)
//                .orElseThrow(() -> new NoSuchElementException("Sender not found with email: " + senderName));
//        log.info("Creating personal chat room for user: " + senderName);
//
//        User receiver = userRepository.getUserByEmail(receiverName)
//                .orElseThrow(() -> new NoSuchElementException("Receiver not found with email: " + receiverName));
//        log.info("Creating personal chat room for user: " + receiverName);
//
//        String roomUUID = chatRoomRepository.createPersonalChatRoom(getPersonalChatRoom(sender, receiver));
//        chatRoomRepository.addChatRoomMembers(roomUUID, List.of(sender.getId(), receiver.getId()));
//
//        return roomUUID;
//    }
    private void saveChatImage(Long chatId, MultipartFile image, Long userId) throws IOException {
        imageService.saveImage(getRequestChatImageDTO(chatId, image, userId));
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

//    private static CreateChatRoomResponseDTO getCreateChatRoomResponseDTO(String roomUUID, String senderName, String receiverName) {
//        return CreateChatRoomResponseDTO.builder()
//                .roomUUID(roomUUID)
//                .senderName(senderName)
//                .receiverName(receiverName)
//                .build();
//    }

    private ChatRoom getPersonalChatRoom(User sender, User receiver){
        return ChatRoom.builder()
                .roomUUID(UUID.randomUUID().toString())
                .createdBy(sender.getId())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .build();
    }

    public String extractChatRoomUUIDFromDestination(String destination) {
        String[] parts = destination.split("/");
        return parts[parts.length - 1];  // 마지막 부분이 chatRoomUUID
    }

    public ChatRoomListResponseDTO getChatRoomList(UserDetailsImpl userDetails, Integer page, Integer size) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Integer offset = (page - 1) * size;
        log.info("username : {}, offset:{}, size:{}", userDetails.getUsername(), offset, size);
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(user.getId(), userDetails.getUsername(), size, offset);
        if (chatRooms.get(0).getChatRoomMemberIds().isEmpty())
            log.info("no chatroom data");
        else
            log.info(chatRooms.get(0).getChatRoomMemberIds().toString());



        List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream()
                .map(chatRoom -> ChatRoomDTO.builder()
                        .chatRoomId(chatRoom.getRoomUUID())
                        .isGroup(chatRoom.isGroup())
                        .roomName(chatRoom.getRoomName())
                        .profileImageUrl(chatRoom.getProfileImageUrl())
                        .senderId(chatRoom.getSenderId())
                        .receiverId(chatRoom.getReceiverId())
                        .chatRoomMemberIds(chatRoom.getChatRoomMemberIds())
                        .lastMessageTime(chatRoom.getLastMessageTime())
                        .lastMessageContent(getLastMessageContent(chatRoom.getRoomUUID()))
                        .unreadCount(chatRoom.getUnreadCount())
                        .build())
                .toList();

        // 4. DTO를 ChatRoomListResponseDTO로 감싸서 반환
        return ChatRoomListResponseDTO.builder()
                .chatRoomDTO(chatRoomDTOs)
                .build();

    }

    public String getLastMessageContent(String chatRoomUUID) {
        return chatRepository.getLastMessageByChatRoomUUID(chatRoomUUID);
    }

}
