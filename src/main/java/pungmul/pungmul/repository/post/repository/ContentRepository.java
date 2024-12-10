package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Content;
import pungmul.pungmul.dto.post.post.ContentUpdateDTO;

import java.util.Optional;

public interface ContentRepository {
    void save(Content content);

    Optional<Content> getContentByPostId(Long postId);

    void updateContentById(ContentUpdateDTO contentUpdateDTO);



}
