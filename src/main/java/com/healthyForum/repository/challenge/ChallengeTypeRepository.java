package com.healthyForum.repository.challenge;

import com.healthyForum.model.challenge.ChallengeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTypeRepository extends JpaRepository<ChallengeType, Integer> {
    Optional<ChallengeType> findByName(String name);
}
