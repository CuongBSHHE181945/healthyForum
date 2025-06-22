package com.healthyForum.model.badge;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="badge_source_type")
public class BadgeSourceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;
}
