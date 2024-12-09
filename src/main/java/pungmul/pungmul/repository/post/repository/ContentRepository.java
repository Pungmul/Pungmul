package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.Content;

import java.util.Optional;

public interface ContentRepository {
    void save(Content content);

    Optional<Content> getContentByPostId(Long postId);

    void updateContentById(Long contentId, String text);


}
