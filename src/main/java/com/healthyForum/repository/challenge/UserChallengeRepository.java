package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Integer> {
    List<UserChallenge> findByUser(User user);
    List<UserChallenge> findByUserAndStatus(User user, String status);
    boolean existsByUserAndChallengeId(User user, int challengeId);
    boolean existsByChallengeId(int challengeId);
    Optional<UserChallenge> findByUserAndChallengeId(User user, int challengeId);
    Optional<UserChallenge> findTopByUserAndChallengeIdOrderByJoinDateDesc(User user, int challengeId);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.challenge.id = :challengeId")
    List<UserChallenge> findAllByChallengeId(Integer challengeId);
}
