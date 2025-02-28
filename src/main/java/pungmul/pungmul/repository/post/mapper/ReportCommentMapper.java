package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.CommentReport;

@Mapper
public interface ReportCommentMapper {

    void reportComment(CommentReport commentReport);

    Integer getReportCountByCommentId(Long commentId);
}
