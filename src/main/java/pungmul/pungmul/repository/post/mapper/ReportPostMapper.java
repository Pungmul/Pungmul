package pungmul.pungmul.repository.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.post.ReportPost;

@Mapper
public interface ReportPostMapper {
    void reportPost(ReportPost reportPost);

    ReportPost getReportPost(Long id);

    Integer getReportCountByPostId(Long postId);
}
