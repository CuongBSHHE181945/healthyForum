package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "leaderboard_entry")
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_challenge_id", nullable = false)
    private CommunityChallenge communityChallenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // null for team entries

    @ManyToOne
    @JoinColumn(name = "team_id")
    private ChallengeTeam team; // null for individual entries

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "entry_type", nullable = false)
    private String entryType; // INDIVIDUAL, TEAM

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"community_challenge_id", "user_id", "entry_type"}),
        @UniqueConstraint(columnNames = {"community_challenge_id", "team_id", "entry_type"})
    })
    public static class LeaderboardConstraint {}
}