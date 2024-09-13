package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.image.repository.ImageRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileStore fileStore;
    private final ImageRepository imageRepository;
    private final DomainImageService domainImageService;
    private final UserRepository userRepository;

//    public Image addChatImage(Long accountId, MultipartFile file) {
//        try {
//            Long userId = userRepository.getUserIdByAccountId(accountId);
//            RequestImageDTO requestImageDTO = new RequestImageDTO(userId, DomainType.CHAT, file);
//            return saveImage(requestImageDTO);
//        } catch (IOException e) {
//            // 로깅 등을 여기서 수행할 수 있음
//            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
//        }
//    }

    public void saveImage(RequestImageDTO requestImageDTO) throws IOException {
        Image image = fileStore.saveImageToLocal(requestImageDTO.getUserId(), requestImageDTO.getImageFile());
        saveImageToRepo(requestImageDTO.getDomainId(), requestImageDTO.getDomainType(), image,requestImageDTO.getUserId());
    }

    private void saveImageToRepo(Long domainId, DomainType domainType, Image image,Long userId) {
        imageRepository.save(image);
        domainImageService.saveDomainImage(domainType, domainId, image.getId(), userId);
    }
}
