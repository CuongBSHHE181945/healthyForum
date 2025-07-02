package com.healthyForum.repository.challenge;

import com.healthyForum.model.challenge.CommunityChallenge;
import com.healthyForum.model.challenge.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {
    List<LeaderboardEntry> findByCommunityChallengeAndEntryTypeOrderByRankPosition(CommunityChallenge communityChallenge, String entryType);
    
    @Query("SELECT le FROM LeaderboardEntry le WHERE le.communityChallenge = :challenge AND le.entryType = :entryType ORDER BY le.score DESC, le.lastUpdated ASC")
    List<LeaderboardEntry> findByCommunityChallengeAndEntryTypeOrderByScore(@Param("challenge") CommunityChallenge challenge, @Param("entryType") String entryType);
    
    @Query("SELECT le FROM LeaderboardEntry le WHERE le.communityChallenge = :challenge ORDER BY le.entryType, le.rankPosition")
    List<LeaderboardEntry> findAllByCommunityChallenge(@Param("challenge") CommunityChallenge challenge);
    
    void deleteByCommunityChallengeAndEntryType(CommunityChallenge communityChallenge, String entryType);
}