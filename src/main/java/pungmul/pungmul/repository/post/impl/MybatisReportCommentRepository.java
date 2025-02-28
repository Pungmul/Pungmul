package pungmul.pungmul.repository.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.post.CommentReport;
import pungmul.pungmul.repository.post.mapper.ReportCommentMapper;
import pungmul.pungmul.repository.post.repository.ReportCommentRepository;

@Repository
@RequiredArgsConstructor
public class MybatisReportCommentRepository implements ReportCommentRepository {
    private final ReportCommentMapper reportCommentMapper;

    @Override
    public void reportComment(CommentReport report) {
        reportCommentMapper.reportComment(report);
    }

    @Override
    public Integer getReportCountByCommentId(Long commentId) {
        return reportCommentMapper.getReportCountByCommentId(commentId);
    }


}
