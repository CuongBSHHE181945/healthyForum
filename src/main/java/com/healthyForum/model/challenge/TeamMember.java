package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "team_member")
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private ChallengeTeam team;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "role", nullable = false)
    private String role = "MEMBER"; // CAPTAIN, MEMBER

    @Column(name = "individual_score")
    private Integer individualScore = 0;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, LEFT, KICKED

    @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))
    public static class TeamMemberConstraint {}
}