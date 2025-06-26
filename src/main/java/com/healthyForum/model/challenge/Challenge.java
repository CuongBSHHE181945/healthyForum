package com.healthyForum.model.challenge;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="challenge")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ChallengeType type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ChallengeCategory category;
}
