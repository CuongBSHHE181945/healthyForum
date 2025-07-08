package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "user_challenge")
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @Column(name = "last_check_date", nullable = false)
    private LocalDate lastCheckDate;
}

