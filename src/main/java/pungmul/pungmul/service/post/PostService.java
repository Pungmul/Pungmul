package pungmul.pungmul.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.domain.post.Post;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.post.RequestPostDTO;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;
import pungmul.pungmul.service.file.DomainImageService;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final DomainImageService domainImageService;

    @Transactional
    public Long addPost(Long userId, RequestPostDTO requestPostDTO) throws IOException {
        log.info("PostService");
        Long postId = savePost(requestPostDTO);
        saveContent(userId, postId, requestPostDTO);

        return postId;
    }

    private Long savePost(RequestPostDTO requestPostDTO) throws IOException {
        Post post = getPost(requestPostDTO);

        log.info("save Post");
        postRepository.save(post);
        log.info("save Post done, {}", post.getId());
        return post.getId();
    }

    private void saveContent(Long userId, Long postId, RequestPostDTO requestPostDTO) throws IOException {
        Content content = getContent(userId, postId, requestPostDTO);
        contentRepository.save(content);

        log.info("save Content id {}", content.getId());
        List<Long> imageIdList = saveImage(userId, content.getId(), requestPostDTO.getFiles());
        content.setImageIdList(imageIdList);

        log.info("save Content done, {}", content.getId());
    }

    private List<Long> saveImage(Long userId, Long contentId, List<MultipartFile> images) throws IOException {
        ArrayList<Long> imageIdList = new ArrayList<>();

        for (MultipartFile image : images) {
            Image savedImage = getImage(userId, image, imageIdList);
            log.info("save Image done, {}", savedImage.getId());
            domainImageService.saveDomainImage(DomainType.CONTENT, contentId, savedImage.getId());
            log.info("save domainImage done, {}", savedImage.getId());
        }
        return imageIdList;
    }

    private static Post getPost(RequestPostDTO requestPostDTO) {
        Post post = Post.builder()
                .categoryId(requestPostDTO.getCategoryId())
                .build();
        return post;
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
                .build();
        Image savedImage = imageService.saveImage(imageDTO);
        log.info("domainId : {}", savedImage.getId());
        imageIdList.add(savedImage.getId());
        return savedImage;
    }

}
