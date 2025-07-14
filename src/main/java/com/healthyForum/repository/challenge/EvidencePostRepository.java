package com.healthyForum.repository.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidencePostRepository extends JpaRepository<EvidencePost, Long> {
    List<EvidencePost> findByStatus(EvidenceStatus status);
    List<EvidencePost> findByUserChallenge_User(User user);
}