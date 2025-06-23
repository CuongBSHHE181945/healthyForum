package com.healthyForum.repository.challenge;

import com.healthyForum.model.challenge.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

}
