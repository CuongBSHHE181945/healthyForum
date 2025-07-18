package com.healthyForum.repository.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    List<Challenge> findByType_Id(Integer typeId);

    List<Challenge> findByCreator(User creator);
}
