package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeActivity;
import com.healthyForum.model.challenge.CommunityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeActivityRepository extends JpaRepository<ChallengeActivity, Long> {
    List<ChallengeActivity> findByCommunityChallenge(CommunityChallenge communityChallenge);
    List<ChallengeActivity> findByUser(User user);
    List<ChallengeActivity> findByCommunityChallengeAndUser(CommunityChallenge communityChallenge, User user);
    List<ChallengeActivity> findByActivityType(String activityType);
    
    @Query("SELECT ca FROM ChallengeActivity ca WHERE ca.communityChallenge = :challenge ORDER BY ca.activityDate DESC")
    List<ChallengeActivity> findRecentActivities(@Param("challenge") CommunityChallenge challenge);
    
    @Query("SELECT ca FROM ChallengeActivity ca WHERE ca.communityChallenge = :challenge AND ca.activityDate >= :startDate ORDER BY ca.activityDate DESC")
    List<ChallengeActivity> findActivitiesSince(@Param("challenge") CommunityChallenge challenge, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(ca.pointsEarned) FROM ChallengeActivity ca WHERE ca.communityChallenge = :challenge AND ca.user = :user AND ca.verified = true")
    Integer getTotalPointsForUser(@Param("challenge") CommunityChallenge challenge, @Param("user") User user);
}