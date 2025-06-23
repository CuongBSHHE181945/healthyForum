package com.healthyForum.repository.challenge;

import com.healthyForum.model.challenge.UserChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserChallengeProgressRepository extends JpaRepository<UserChallengeProgress, Integer> {
    boolean existsByUserChallengeIdAndDate(int userChallengeId, LocalDate date);
    List<UserChallengeProgress> findByUserChallengeIdOrderByDateAsc(int userChallengeId);
}