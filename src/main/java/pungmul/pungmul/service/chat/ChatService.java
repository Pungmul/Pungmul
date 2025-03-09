package pungmul.pungmul.service.chat;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.repository.chat.repository.ChatRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomMemberRepository;
import pungmul.pungmul.repository.chat.repository.ChatRoomRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.MemberService;
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
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final MemberService memberService;

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
    public CreateChatRoomResponseDTO createPersonalChatRoom(String senderName, String receiverName) {
        // 1. 기존 채팅방 조회
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findChatRoomByUsers(senderName, receiverName);

        // 2. 기존 채팅방이 존재하면 UUID 반환
        if (existingChatRoom.isPresent()) {
//            return buildChatRoomResponseDTO(existingChatRoom.get().getRoomUUID());
            return CreateChatRoomResponseDTO.builder()
                    .isCreated(false)
                    .roomUUID(existingChatRoom.get().getRoomUUID())
                    .build();
        }

        // 3. 새 채팅방 생성 및 멤버 추가
        String newChatRoomUUID = createNewPersonalChatRoom(senderName, receiverName);

        // 4. 생성된 채팅방 정보 반환
        return CreateChatRoomResponseDTO.builder()
                .isCreated(true)
                .roomUUID(newChatRoomUUID)
                .build();
    }

    @Transactional
    public CreateChatRoomResponseDTO createMultiChatRoom(String senderName, List<String> receiverNameList){
        String newChatRoomUUID = createNewMultiChatRoom(senderName, receiverNameList);

        return CreateChatRoomResponseDTO.builder()
                .isCreated(true)
                .roomUUID(newChatRoomUUID)
                .build();
//        return buildChatRoomResponseDTO(newChatRoomUUID);
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

    private SimpleChatRoomDTO buildChatRoomResponseDTO(String chatRoomUUID, Long userId) {
        // 채팅방 기본 정보 조회
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByUUID(chatRoomUUID);
        ChatMessage lastMessage = chatRepository.getLastMessageByChatRoomUUID(chatRoomUUID);

        // 공통 데이터 설정
        SimpleChatRoomDTO.SimpleChatRoomDTOBuilder builder = SimpleChatRoomDTO.builder()
                .chatRoomUUID(chatRoomUUID)
                .isGroup(chatRoom.isGroup())
                .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                .lastMessageContent(lastMessage != null ? lastMessage.getContent() : null)
                .unreadCount(null)  // unreadCount 로직 구현 후 추가
//                .unreadCount(chatRoom.getUnreadCount() != null ? chatRoom.getUnreadCount() : 0)
                .roomName(
                        chatRoom.isGroup() ?
                                (chatRoom.getRoomName() != null ? chatRoom.getRoomName() : String.join(", ", getOpponentMultiMemberNameList(chatRoomUUID, userId)))
                                : getOpponentName(chatRoomUUID, userId)
                )
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

    private List<String> getOpponentMultiMemberNameList(String chatRoomUUID, Long userId) {
        List<Long> opponentMultiMemberIdList = chatRoomMemberRepository.getOpponentMultiMemberNameList(chatRoomUUID, userId);
        return opponentMultiMemberIdList.stream().map(userService::getUserById).map(User::getName).toList();
    }

    private String getOpponentName(String chatRoomUUID, Long userId) {
        Long opponentUserId = chatRoomMemberRepository.getOpponentUserId(chatRoomUUID, userId);
        return userService.getUserById(opponentUserId).getName();
    }

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
//                .receiverUsername(chatMessageRequestDTO.getReceiverUsername())
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

        // 해당하는 채팅방 없음
        if (chatRooms.isEmpty()) {
            log.info("No chat room data found for user: {}", userDetails.getUsername());
            return ChatRoomListResponseDTO.builder()
                    .simpleChatRoomDTOList(Collections.emptyList()) // 빈 리스트 반환
                    .build();
        }

        // ChatRoomDTO 리스트 생성
        List<SimpleChatRoomDTO> simpleChatRoomDTOList = chatRooms.stream()
                .map(chatRoom -> buildChatRoomResponseDTO(chatRoom.getRoomUUID(), user.getId())) // UUID를 기반으로 DTO 생성
                .toList();

        // 4. DTO를 ChatRoomListResponseDTO로 감싸서 반환
        return ChatRoomListResponseDTO.builder()
                .simpleChatRoomDTOList(simpleChatRoomDTOList)
                .build();
    }

//    public GetMessagesByChatRoomResponseDTO getMessagesByChatRoom(String chatRoomUUID, int page, int size) {
//        int offset = (page - 1) * size; // 역순으로 로드
//        List<ChatMessage> messages = chatRepository.getMessagesByChatRoom(chatRoomUUID, size, offset);
//        List<ChatMessage> chatMessageList = messages.stream()
//                .sorted(Comparator.comparing(ChatMessage::getCreatedAt)) // 시간 순서로 정렬
//                .toList();
//
//        return GetMessagesByChatRoomResponseDTO.builder()
//                .chatMessageList(chatMessageList)
//                .build();
//    }

    public GetMessagesByChatRoomResponseDTO getMessagesByChatRoom(String chatRoomUUID, int page, int size) {
        PageHelper.startPage(page, size);
        List<ChatMessage> messages = chatRepository.getMessagesByChatRoomUUID(chatRoomUUID);

//        List<ChatMessage> chatMessageList = messages.stream()
//                .sorted(Comparator.comparing(ChatMessage::getCreatedAt)) // 시간 순서로 정렬
//                .toList();

        return GetMessagesByChatRoomResponseDTO.builder()
                .chatMessageList(new PageInfo<>(messages))
                .build();
    }

    public GetChatRoomInfoResponseDTO getChatRoomInfo(String chatRoomUUID, UserDetailsImpl userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByUUID(chatRoomUUID);
        ChatRoomDTO chatRoomDTO = charRoomDTO(chatRoomUUID, chatRoom, user);

        List<Long> allMembersByChatRoomId = chatRoomMemberRepository.findAllMembersByChatRoomId(chatRoomUUID);
        List<SimpleUserDTO> simpleUserDTOList = allMembersByChatRoomId.stream().map(memberService::getSimpleUserDTO).toList();

        GetMessagesByChatRoomResponseDTO messagesByChatRoom = getMessagesByChatRoom(chatRoomUUID, 1, 20);

        return GetChatRoomInfoResponseDTO.builder()
                .chatRoomInfo(chatRoomDTO)
                .userInfoList(simpleUserDTOList)
                .messageList(messagesByChatRoom.getChatMessageList())
                .build();
    }

    private ChatRoomDTO charRoomDTO(String chatRoomUUID, ChatRoom chatRoom, User user) {
        return ChatRoomDTO.builder()
                .chatRoomUUID(chatRoomUUID)
                .isGroup(chatRoom.isGroup())
                .roomName(
                        chatRoom.isGroup() ?
                        (chatRoom.getRoomName() != null ? chatRoom.getRoomName() : String.join(", ", getOpponentMultiMemberNameList(chatRoomUUID, user.getId())))
                        : getOpponentName(chatRoomUUID, user.getId())
                )
                .profileImageUrl(chatRoom.getProfileImageUrl())
                .build();
    }
}
