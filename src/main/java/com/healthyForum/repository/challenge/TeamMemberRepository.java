package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeTeam;
import com.healthyForum.model.challenge.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeam(ChallengeTeam team);
    List<TeamMember> findByUser(User user);
    List<TeamMember> findByTeamAndStatus(ChallengeTeam team, String status);
    Optional<TeamMember> findByTeamAndUser(ChallengeTeam team, User user);
    
    @Query("SELECT tm FROM TeamMember tm WHERE tm.user = :user AND tm.team.communityChallenge.id = :challengeId AND tm.status = 'ACTIVE'")
    Optional<TeamMember> findActiveTeamMembershipForChallenge(@Param("user") User user, @Param("challengeId") Long challengeId);
    
    boolean existsByTeamAndUserAndStatus(ChallengeTeam team, User user, String status);
    
    @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.team = :team AND tm.status = 'ACTIVE'")
    Integer countActiveMembers(@Param("team") ChallengeTeam team);
}