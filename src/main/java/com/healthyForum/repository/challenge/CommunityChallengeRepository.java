package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.CommunityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CommunityChallengeRepository extends JpaRepository<CommunityChallenge, Long> {
    List<CommunityChallenge> findByStatus(String status);
    List<CommunityChallenge> findByIsPublicTrueAndStatus(String status);
    List<CommunityChallenge> findByCreator(User creator);
    List<CommunityChallenge> findByCategoryId(Integer categoryId);
    
    @Query("SELECT cc FROM CommunityChallenge cc WHERE cc.isPublic = true AND cc.status = 'ACTIVE' AND cc.startDate <= :currentDate AND cc.endDate >= :currentDate")
    List<CommunityChallenge> findActivePublicChallenges(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT cc FROM CommunityChallenge cc WHERE cc.isPublic = true AND cc.status = 'UPCOMING' AND cc.startDate > :currentDate")
    List<CommunityChallenge> findUpcomingPublicChallenges(@Param("currentDate") LocalDate currentDate);
}