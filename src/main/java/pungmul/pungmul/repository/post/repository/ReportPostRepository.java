package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.ReportPost;

public interface ReportPostRepository {
    public void reportPost(ReportPost reportPost);

    ReportPost getReportPost(Long id);

    Integer getReportCountByPostId(Long postId);
}
