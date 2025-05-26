package com.healthyForum.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sleep_entries")
public class SleepEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sleepId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private int quality;

    @Column(length = 255)
    private String notes;

    // 🔗 Link to User
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    // Getters and setters...
}
