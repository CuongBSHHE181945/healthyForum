package com.healthyForum.repository.challenge;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.challenge.EvidenceReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvidenceReactionRepository extends JpaRepository<EvidenceReaction, Integer> {

    Optional<EvidenceReaction> findByUserIdAndEvidencePostId(Long userId, Integer evidencePostId);

    long countByEvidencePostIdAndReactionType(Integer evidencePostId, ReactionType reactionType);

    @Query("""
    SELECT COUNT(er)
    FROM EvidenceReaction er
    JOIN er.evidencePost ep
    WHERE ep.post.id = :postId AND er.reactionType = :reactionType
""")
    long countByPostIdAndReactionType(@Param("postId") Long postId, @Param("reactionType") ReactionType reactionType);

    // Find if a specific user has reacted to a post
    @Query("""
    SELECT r FROM EvidenceReaction r
    WHERE r.evidencePost.post.id = :postId
      AND r.user.id = :userId
    """)
    Optional<EvidenceReaction> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
