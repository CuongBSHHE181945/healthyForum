package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "community_challenge")
public class CommunityChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "scoring_criteria", columnDefinition = "TEXT")
    private String scoringCriteria;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_team_based", nullable = false)
    private Boolean isTeamBased = false;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "status", nullable = false)
    private String status = "UPCOMING"; // UPCOMING, ACTIVE, COMPLETED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ChallengeCategory category;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}