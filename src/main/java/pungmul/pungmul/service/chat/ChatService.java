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
import pungmul.pungmul.dto.chat.*;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.chat.repository.ChatRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ImageService imageService;
    private final UserService userService;

//    @Transactional
//    public CreatePersonalChatRoomResponseDTO createPersonalChatRoom(String senderName, String receiverName) {
//        // 1. 기존 채팅방 존재 여부 확인
//        Optional<ChatRoom> existChatRoom = chatRoomRepository.findChatRoomByUsers(senderName, receiverName).map();
//
//        // 2. 채팅방 UUID 처리
//        String chatRoomUUID;
//        if (existChatRoom.isPresent()) {
//            chatRoomUUID = existChatRoom.get().getRoomUUID();
//        } else {
//            // 1. 채팅방 생성
//            String roomUUID = createDefaultChatRoom(senderName);
//
//            // 2. 채팅방 멤버 추가
//            Long senderId = userService.getUserByEmail(senderName).getId();
//            Long receiverId = userService.getUserByEmail(receiverName).getId();
//            chatRoomRepository.addChatRoomMembers(roomUUID, List.of(senderId, receiverId));
//        }
//
//        // 3. 생성된 채팅방 정보 반환
//        return getCreateChatRoomResponseDTO(chatRoomUUID, senderName, receiverName);
//    }

    @Transactional
    public ChatRoomDTO createPersonalChatRoom(String senderName, String receiverName) {
        // 1. 기존 채팅방 조회
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findChatRoomByUsers(senderName, receiverName);

        // 2. 기존 채팅방이 존재하면 UUID 반환
        if (existingChatRoom.isPresent()) {
            return buildChatRoomResponseDTO(existingChatRoom.get().getRoomUUID());
        }

        // 3. 새 채팅방 생성 및 멤버 추가
        String newChatRoomUUID = createNewPersonalChatRoom(senderName, receiverName);

        // 4. 생성된 채팅방 정보 반환
        return buildChatRoomResponseDTO(newChatRoomUUID);
    }

    @Transactional
    public ChatRoomDTO createMultiChatRoom(String senderName, List<String> receiverNameList){
        String newChatRoomUUID = createNewMultiChatRoom(senderName, receiverNameList);

        return buildChatRoomResponseDTO(newChatRoomUUID);
    }

    private String createNewPersonalChatRoom(String senderName, String receiverName) {
        // 1. 기본 채팅방 생성
       ChatRoom chatRoom = buildPersonalChatRoom(senderName);
       chatRoomRepository.createChatRoom(chatRoom);

        // 2. 채팅방 멤버 추가
        Long senderId = userService.getUserByEmail(senderName).getId();
        Long receiverId = userService.getUserByEmail(receiverName).getId();
        chatRoomRepository.addChatRoomMembers(chatRoom.getRoomUUID(), List.of(senderId, receiverId));

        return chatRoom.getRoomUUID();
    }

    private String createNewMultiChatRoom(String senderName, List<String> receiverNameList) {
        //  1. 기본 채팅방 생성
        ChatRoom chatRoom = buildMultiChatRoom(senderName);
        chatRoomRepository.createChatRoom(chatRoom);

        //  2. 채팅방 멤버 추가
        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(userService.getUserByEmail(senderName).getId());
        memberIdList.addAll(receiverNameList.stream()
                .map(receiverName -> userService.getUserByEmail(receiverName).getId())
                .toList());

        chatRoomRepository.addChatRoomMembers(chatRoom.getRoomUUID(), memberIdList);

        return chatRoom.getRoomUUID();
    }

    private ChatRoom buildPersonalChatRoom(String senderName) {
        // 채팅방 UUID 생성 및 기본 정보 설정
        String roomUUID = UUID.randomUUID().toString();
        return ChatRoom.builder()
                .roomUUID(roomUUID)
                .createdBy(userService.getUserByEmail(senderName).getId())
                .isGroup(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ChatRoom buildMultiChatRoom(String senderName){
        String roomUUID = UUID.randomUUID().toString();
        return ChatRoom.builder()
                .roomUUID(roomUUID)
                .createdBy(userService.getUserByEmail(senderName).getId())
                .isGroup(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ChatRoomDTO buildChatRoomResponseDTO(String chatRoomUUID) {
        // 채팅방 기본 정보 조회
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByUUID(chatRoomUUID);
        ChatMessage lastMessage = chatRepository.getLastMessageByChatRoomUUID(chatRoomUUID);

        // 공통 데이터 설정
        ChatRoomDTO.ChatRoomDTOBuilder builder = ChatRoomDTO.builder()
                .chatRoomUUID(chatRoomUUID)
                .isGroup(chatRoom.isGroup())
                .lastMessageTime(lastMessage != null ? lastMessage.getTimestamp() : null)
                .lastMessageContent(lastMessage != null ? lastMessage.getContent() : null)
                .unreadCount(chatRoom.getUnreadCount() != null ? chatRoom.getUnreadCount() : 0)
                .roomName(chatRoom.getRoomName())
                .profileImageUrl(chatRoom.getProfileImageUrl());

        // 채팅방 유형별 데이터 설정
        if (chatRoom.isGroup()) {
            // 단체 채팅방: 멤버 정보 추가
            List<Long> memberIds = chatRoomRepository.findChatRoomMemberList(chatRoomUUID);
            List<String> memberNames = memberIds.stream()
                    .map(userService::getUserById)
                    .map(User :: getName)
                    .collect(Collectors.toList());

            builder.chatRoomMemberIds(memberIds)
                    .chatRoomMemberNames(memberNames);
        } else {
            // 1:1 채팅방: 송신자와 수신자 정보 추가
            List<Long> chatRoomMemberList = chatRoomRepository.findChatRoomMemberList(chatRoomUUID);
            Long receiverId = chatRoomMemberList.stream()
                    .filter(memberId -> !memberId.equals(chatRoom.getCreatedBy())) // createdBy와 일치하지 않는 ID 필터링
                    .findFirst() // 첫 번째 결과 가져오기
                    .orElseThrow(() -> new IllegalArgumentException("No receiver found for the chat room"));

            builder.senderId(chatRoom.getCreatedBy())
                    .senderName(userService.getUserById(chatRoom.getCreatedBy()).getName())
                    .receiverId(receiverId) // createdBy는 receiverId로 설정
                    .receiverName(userService.getUserById(receiverId).getName());
        }
        return builder.build();
    }

//    private String createDefaultChatRoom(String senderName) {
//        String roomUUID = UUID.randomUUID().toString();
//        ChatRoom chatRoom = ChatRoom.builder()
//                .roomUUID(roomUUID)
//                .createdBy(userService.getUserByEmail(senderName).getId()) // 생성자 ID
//                .isGroup(false) // 개인 DM 방이므로 false
//                .createdAt(LocalDateTime.now())
//                .build();
//        chatRoomRepository.createChatRoom(chatRoom);
//        return roomUUID;
//    }


//    public String createPersonalChatRoom(String senderName, String receiverName) {
//        // 1. 채팅방 생성
//        String roomUUID = createDefaultChatRoom(senderName);
//
//        // 2. 채팅방 멤버 추가
//        Long senderId = userService.getUserByEmail(senderName).getId();
//        Long receiverId = userService.getUserByEmail(receiverName).getId();
//        chatRoomRepository.addChatRoomMembers(roomUUID, List.of(senderId, receiverId));
//
//        return roomUUID;
//    }
/*
    private CreatePersonalChatRoomResponseDTO getCreateChatRoomResponseDTO(String chatRoomUUID, String senderName, String receiverName) {
        return CreatePersonalChatRoomResponseDTO.builder()
                .roomUUID(chatRoomUUID)
                .senderName(senderName)
                .receiverName(receiverName)
                .build();
    }

 */

    @Transactional
    public ChatMessage saveMessage(String senderName, String chatRoomUUID, ChatMessageRequestDTO chatMessageRequestDTO) {
        log.info("sender name: {}, chat room uuid: {}", senderName, chatRoomUUID);

        ChatMessage message = chatRepository.save(getChatMessage(senderName, chatRoomUUID, chatMessageRequestDTO));
        log.info("messageId : {}", message.getId());
        chatRoomRepository.updateLastMessage(chatRoomUUID, message.getId());

        return message;
    }

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

    public String extractChatRoomUUIDFromDestination(String destination) {
        String[] parts = destination.split("/");
        return parts[parts.length - 1];  // 마지막 부분이 chatRoomUUID
    }

    public ChatRoomListResponseDTO getChatRoomList(UserDetailsImpl userDetails, Integer page, Integer size) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Integer offset = (page - 1) * size;
        log.info("username : {}, offset:{}, size:{}", userDetails.getUsername(), offset, size);
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(user.getId(), userDetails.getUsername(), size, offset);

        if (chatRooms.isEmpty()) {
            log.info("No chat room data found for user: {}", userDetails.getUsername());
            return ChatRoomListResponseDTO.builder()
                    .chatRoomDTOList(Collections.emptyList()) // 빈 리스트 반환
                    .build();
        }

        // ChatRoomDTO 리스트 생성
        List<ChatRoomDTO> chatRoomDTOList = chatRooms.stream()
                .map(chatRoom -> buildChatRoomResponseDTO(chatRoom.getRoomUUID())) // UUID를 기반으로 DTO 생성
                .toList();

        // 4. DTO를 ChatRoomListResponseDTO로 감싸서 반환
        return ChatRoomListResponseDTO.builder()
                .chatRoomDTOList(chatRoomDTOList)
                .build();
    }

//    public GetMessagesByChatRoomResponseDTO getMessagesByChatRoom(String chatRoomUUID) {
//        List<ChatMessage> messagesByChatRoom = chatRepository.getMessagesByChatRoom(chatRoomUUID);
//        return GetMessagesByChatRoomResponseDTO.builder()
//                .chatMessageList(messagesByChatRoom)
//                .build();
//    }

    public GetMessagesByChatRoomResponseDTO getMessagesByChatRoom(String chatRoomUUID, int page, int size) {
        int offset = (page - 1) * size; // 역순으로 로드
        List<ChatMessage> messages = chatRepository.getMessagesByChatRoom(chatRoomUUID, size, offset);
        List<ChatMessage> chatMessageList = messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp)) // 시간 순서로 정렬
                .toList();

        return GetMessagesByChatRoomResponseDTO.builder()
                .chatMessageList(chatMessageList)
                .build();
    }
}
