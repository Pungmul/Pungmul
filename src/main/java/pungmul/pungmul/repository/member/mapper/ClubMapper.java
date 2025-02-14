package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.club.Club;
import pungmul.pungmul.dto.member.ClubInfo;

import java.util.List;

@Mapper
public interface ClubMapper {
    public List<Club> getClubList();

    String getGroupName(Long clubId);

    Club getClubInfo(Long clubId);
}
