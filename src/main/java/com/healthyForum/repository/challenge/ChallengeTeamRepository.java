package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeTeam;
import com.healthyForum.model.challenge.CommunityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeTeamRepository extends JpaRepository<ChallengeTeam, Long> {
    List<ChallengeTeam> findByCommunityChallenge(CommunityChallenge communityChallenge);
    List<ChallengeTeam> findByCaptain(User captain);
    List<ChallengeTeam> findByStatus(String status);
    
    @Query("SELECT ct FROM ChallengeTeam ct WHERE ct.communityChallenge = :challenge AND ct.status = 'ACTIVE' AND ct.currentMembers < ct.maxMembers")
    List<ChallengeTeam> findAvailableTeams(@Param("challenge") CommunityChallenge challenge);
    
    @Query("SELECT ct FROM ChallengeTeam ct WHERE ct.communityChallenge = :challenge ORDER BY ct.totalScore DESC")
    List<ChallengeTeam> findTeamsOrderedByScore(@Param("challenge") CommunityChallenge challenge);
}