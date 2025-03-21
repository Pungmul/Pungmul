package pungmul.pungmul.service.post.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.post.PostRequestDTO;
import pungmul.pungmul.dto.post.post.ContentUpdateDTO;
import pungmul.pungmul.dto.post.post.PostResponseDTO;
import pungmul.pungmul.dto.post.post.UpdatePostRequestDTO;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostContentService {
    private final ContentRepository contentRepository;
    private final ImageService imageService;
    private final DomainImageService domainImageService;
    private final UserService userService;

    public void saveContent(Long userId, Long postId, PostRequestDTO postRequestDTO, List<MultipartFile> files) throws IOException {
        Content content = getContent(userId, postId, postRequestDTO);
        contentRepository.save(content);

        if (files != null && !files.isEmpty() && files.stream().anyMatch(file -> !file.isEmpty())) {
            saveImages(content.getId(), files, userId);
        }
    }

    private static Content getContent(Long userId, Long postId, PostRequestDTO postRequestDTO) {
        return Content.builder()
                .postId(postId)
                .title(postRequestDTO.getTitle())
                .text(postRequestDTO.getText())
                .anonymity(postRequestDTO.isAnonymity())
                .writerId(userId)
                .build();
    }

    public PostResponseDTO getPostResponse(Post post) {
        Content content = contentRepository.getContentByPostId(post.getId()).orElseThrow(NoSuchElementException::new);
        return PostResponseDTO.builder()
                .postId(post.getId())
                .title(content.getTitle())
                .content(content.getText())
                .author("Anonymous") // 예시, 필요 시 수정
                .build();
    }

    public void updateContent(UserDetailsImpl userDetails, Long postId, UpdatePostRequestDTO updatePostRequestDTO, List<MultipartFile> files) throws IOException {
        log.info("call updateContent");
        Long contentId = getContentByPostId(postId).getId();
        contentRepository.updateContentById(ContentUpdateDTO.builder()
                .contentId(contentId)
                .anonymity(updatePostRequestDTO.isAnonymity())
                .text(updatePostRequestDTO.getText())
                .build());
        if (!updatePostRequestDTO.getDeleteImageIdList().isEmpty()) {
            log.info("call updateImage");
            deleteContentImage(updatePostRequestDTO.getDeleteImageIdList());
        }

        if (!files.isEmpty()) {
            Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
            saveImageList(userId, contentId, files);
        }
    }

    private void saveImageList(Long userId, Long contentId, List<MultipartFile> images) throws IOException {
        for (MultipartFile image : images)
            saveContentImage(contentId, image, userId);
    }

    private void saveContentImage(Long contentId, MultipartFile image, Long userId) throws IOException {
        imageService.saveImage(getRequestContentImageDTO(contentId, image, userId));
    }

    private RequestImageDTO getRequestContentImageDTO(Long contentId, MultipartFile image, Long userId) {
        return RequestImageDTO.builder()
                .domainId(contentId)
                .imageFile(image)
                .userId(userId)
                .domainType(DomainType.CONTENT)
                .build();
    }

    private void deleteContentImage(List<Long> deleteImageIdList) {
        log.info("deleteImageIdList : {}", deleteImageIdList);
        domainImageService.deleteDomainImage(deleteImageIdList);
    }

    private void saveImages(Long contentId, List<MultipartFile> files, Long userId) throws IOException {
        for (MultipartFile file : files) {
            imageService.saveImage(getRequestContentImageDTO(contentId, file, userId));
        }
    }

    public Content getContentByPostId(Long postId) {
        Content contentByPostId = contentRepository.getContentByPostId(postId).orElseThrow(NoSuchElementException::new);
        List<Image> imagesByDomainId = imageService.getImagesByDomainId(DomainType.CONTENT, contentByPostId.getId());
        contentByPostId.setImageList(imagesByDomainId);

        return contentByPostId;
    }
}
