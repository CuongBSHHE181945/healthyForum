package com.healthyForum.model.challenge;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "user_challenge_progress")
public class UserChallengeProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_challenge_id", nullable = false)
    private UserChallenge userChallenge;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "completed")
    private boolean completed = false;

    // getters/setters
}