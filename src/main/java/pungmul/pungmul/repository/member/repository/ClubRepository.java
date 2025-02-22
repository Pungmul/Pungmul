package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.club.Club;
import pungmul.pungmul.dto.member.ClubInfo;

import java.util.List;


public interface ClubRepository {
    List<Club> getClubList();

    String getGroupName(Long clubId);

    Club getClubInfo(Long clubId);

}
