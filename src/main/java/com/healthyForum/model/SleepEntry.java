package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sleep_entries")
public class SleepEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_id")
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Helper methods
    public long getSleepDurationInMinutes() {
        if (startTime != null && endTime != null) {
            long startMinutes = startTime.toSecondOfDay() / 60;
            long endMinutes = endTime.toSecondOfDay() / 60;
            
            if (endMinutes < startMinutes) {
                // Sleep spans midnight
                endMinutes += 24 * 60;
            }
            
            return endMinutes - startMinutes;
        }
        return 0;
    }

    public double getSleepDurationInHours() {
        return getSleepDurationInMinutes() / 60.0;
    }

    public boolean isGoodSleep() {
        return quality >= 7;
    }
}
