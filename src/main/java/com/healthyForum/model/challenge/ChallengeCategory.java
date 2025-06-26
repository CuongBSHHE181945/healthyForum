package com.healthyForum.model.challenge;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "challenge_category")
public class ChallengeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
