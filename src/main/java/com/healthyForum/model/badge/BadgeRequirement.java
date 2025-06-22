package com.healthyForum.model.badge;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="badge_requirement")
public class BadgeRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @ManyToOne
    @JoinColumn(name = "source_type_id", nullable = false)
    private BadgeSourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private int sourceId;     // ID of the challenge, goal, etc.

    @Column(name = "value", nullable = false)
    private String value;      // Optional: e.g., target score, steps
}
