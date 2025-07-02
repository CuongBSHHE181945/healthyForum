package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "challenge_team")
public class ChallengeTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "community_challenge_id", nullable = false)
    private CommunityChallenge communityChallenge;

    @ManyToOne
    @JoinColumn(name = "captain_id", nullable = false)
    private User captain;

    @Column(name = "max_members")
    private Integer maxMembers = 10;

    @Column(name = "current_members")
    private Integer currentMembers = 1;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, FULL, DISBANDED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}