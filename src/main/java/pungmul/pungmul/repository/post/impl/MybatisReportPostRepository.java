package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.ReportPost;
import pungmul.pungmul.repository.post.mapper.ReportPostMapper;
import pungmul.pungmul.repository.post.repository.ReportPostRepository;

@RequiredArgsConstructor
@Repository
public class MybatisReportPostRepository implements ReportPostRepository {
    private final ReportPostMapper reportPostMapper;
    @Override
    public void reportPost(ReportPost reportPost) {
        reportPostMapper.reportPost(reportPost);
    }

    @Override
    public ReportPost getReportPost(Long id) {
        return reportPostMapper.getReportPost(id);
    }

    @Override
    public Integer getReportCountByPostId(Long postId) {
        return reportPostMapper.getReportCountByPostId(postId);
    }
}
