package com.healthyForum.model.keywordFiltering;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "keyword")
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;
}
