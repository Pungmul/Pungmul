package pungmul.pungmul.repository.post.repository;

import pungmul.pungmul.domain.post.CommentReport;

public interface ReportCommentRepository {

    void reportComment(CommentReport report);

    Integer getReportCountByCommentId(Long commentId);

}
