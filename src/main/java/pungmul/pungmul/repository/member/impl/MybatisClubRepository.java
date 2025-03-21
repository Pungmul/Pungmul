package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.club.Club;
import pungmul.pungmul.dto.member.ClubInfo;
import pungmul.pungmul.repository.member.mapper.ClubMapper;
import pungmul.pungmul.repository.member.repository.ClubRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MybatisClubRepository implements ClubRepository {
    private final ClubMapper clubMapper;
    @Override
    public List<Club> getClubList() {
        return clubMapper.getClubList();
    }

    @Override
    public String getGroupName(Long clubId) {
        return clubMapper.getGroupName(clubId);
    }

    @Override
    public Club getClubInfo(Long clubId) {
        return clubMapper.getClubInfo(clubId);
    }
}
