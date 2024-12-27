package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.club.Club;

import java.util.List;


public interface ClubRepository {
    public List<Club> getClubList();

    String getGroupName(Long clubId);

}
