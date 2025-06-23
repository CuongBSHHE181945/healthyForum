package com.healthyForum.model.challenge;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="challenge_type")
public class ChallengeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name" , unique = true, nullable = false)
    private String name; // PERSONAL, PUBLIC
}
