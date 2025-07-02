package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "challenge_participation")
public class ChallengeParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_challenge_id", nullable = false)
    private CommunityChallenge communityChallenge;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private ChallengeTeam team; // null for individual challenges

    @Column(name = "individual_score")
    private Integer individualScore = 0;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, DROPPED_OUT

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"community_challenge_id", "user_id"}))
    public static class ParticipationConstraint {}
}