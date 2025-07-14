package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Integer> {
    List<UserChallenge> findByUser(User user);
    List<UserChallenge> findByUserAndStatus(User user, String status);
    boolean existsByUserAndChallengeId(User user, int challengeId);
    Optional<UserChallenge> findByUserAndChallengeId(User user, int challengeId);
    Optional<UserChallenge> findTopByUserAndChallengeIdOrderByJoinDateDesc(User user, int challengeId);
}
