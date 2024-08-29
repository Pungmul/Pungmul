package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final DomainImageService domainImageService;
    private final UserRepository userRepository;

    @Transactional
    public Long addPost(Long accountId, RequestPostDTO requestPostDTO, List<MultipartFile> files) throws IOException {
        Long postId = savePost(requestPostDTO);
        saveContent(userRepository.getUserIdByAccountId(accountId), postId, requestPostDTO, files);

        return postId;
    }

    private Long savePost(RequestPostDTO requestPostDTO) throws IOException {
        Post post = getPost(requestPostDTO);
        postRepository.save(post);

        return post.getId();
    }

    private void saveContent(Long userId, Long postId, RequestPostDTO requestPostDTO, List<MultipartFile> files) throws IOException {
        Content content = getContent(userId, postId, requestPostDTO);
        contentRepository.save(content);

        saveImage(userId, content.getId(),files);
//        content.setImageIdList(imageIdList);
    }

    private List<Long> saveImage(Long userId, Long contentId, List<MultipartFile> images) throws IOException {
        ArrayList<Long> imageIdList = new ArrayList<>();

        for (MultipartFile image : images) {
            Image savedImage = getImage(userId, image, imageIdList);
            domainImageService.saveDomainImage(DomainType.CONTENT, contentId, savedImage.getId());
        }
        return imageIdList;
    }

    private static Post getPost(RequestPostDTO requestPostDTO) {
        return Post.builder()
                .categoryId(requestPostDTO.getCategoryId())
                .build();
    }

    private static Content getContent(Long userId, Long postId, RequestPostDTO requestPostDTO) {
        return Content.builder()
                .postId(postId)
                .title(requestPostDTO.getTitle())
                .text(requestPostDTO.getText())
                .anonymity(requestPostDTO.isAnonymity())
                .writerId(userId)
                .build();
    }

    private Image getImage(Long userId, MultipartFile image, ArrayList<Long> imageIdList) throws IOException {
        RequestImageDTO imageDTO = RequestImageDTO.builder()
                .imageFile(image)
                .userId(userId)
                .domainType(DomainType.CONTENT)
                .build();
        Image savedImage = imageService.saveImage(imageDTO);
        log.info("domainId : {}", savedImage.getId());
        imageIdList.add(savedImage.getId());
        return savedImage;
    }

    public Integer likePost(Long accountId, Long postId) {
        postRepository.likePost(userRepository.getUserIdByAccountId(accountId), postId);
        postRepository.plusPostLikeCount(postId);

        return postRepository.postLikedNum(postId);
    }

}
