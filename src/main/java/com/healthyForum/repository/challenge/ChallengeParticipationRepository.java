package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeParticipation;
import com.healthyForum.model.challenge.CommunityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {
    List<ChallengeParticipation> findByUser(User user);
    List<ChallengeParticipation> findByCommunityChallenge(CommunityChallenge communityChallenge);
    List<ChallengeParticipation> findByUserAndStatus(User user, String status);
    Optional<ChallengeParticipation> findByCommunityChallengeAndUser(CommunityChallenge communityChallenge, User user);
    
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.communityChallenge = :challenge AND cp.status = 'ACTIVE' ORDER BY cp.individualScore DESC")
    List<ChallengeParticipation> findActiveParticipantsOrderedByScore(@Param("challenge") CommunityChallenge challenge);
    
    @Query("SELECT COUNT(cp) FROM ChallengeParticipation cp WHERE cp.communityChallenge = :challenge AND cp.status = 'ACTIVE'")
    Integer countActiveParticipants(@Param("challenge") CommunityChallenge challenge);
    
    boolean existsByCommunityChallengeAndUserAndStatus(CommunityChallenge communityChallenge, User user, String status);
}