package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.club.Club;

import java.util.List;

@Mapper
public interface ClubMapper {
    public List<Club> getClubList();
}
