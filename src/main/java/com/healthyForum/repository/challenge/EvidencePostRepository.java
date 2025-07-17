package com.healthyForum.repository.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidencePostRepository extends JpaRepository<EvidencePost, Integer> {
    List<EvidencePost> findByStatus(EvidenceStatus status);
    List<EvidencePost> findByUserChallenge_User(User user);
    List<EvidencePost> findByUserChallengeId(Integer userChallengeId);

    @Query("SELECT e FROM EvidencePost e JOIN FETCH e.userChallenge uc WHERE uc.id IN :userChallengeIds")
    List<EvidencePost> findAllByUserChallengeIds(List<Integer> userChallengeIds); // ✅ works

    @Query("""
    SELECT e FROM EvidencePost e 
    JOIN FETCH e.userChallenge uc 
    WHERE uc.id IN :userChallengeIds AND uc.user.id <> :currentUserId
    """)
    List<EvidencePost> findAllByUserChallengeIdsExcludingUser(
            @Param("userChallengeIds") List<Integer> userChallengeIds,
            @Param("currentUserId") Integer currentUserId
    );
}