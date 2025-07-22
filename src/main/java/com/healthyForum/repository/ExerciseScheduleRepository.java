package com.healthyForum.repository;

import com.healthyForum.model.ExerciseSchedule;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExerciseScheduleRepository extends JpaRepository<ExerciseSchedule, Long> {

    /**
     * Finds all exercise schedules for a given user and date, ordered by time ascending.
     *
     * @param user The user entity.
     * @param date The date of the exercises.
     * @return A list of exercise schedules.
     */
    List<ExerciseSchedule> findAllByUserAndDateOrderByTimeAsc(User user, LocalDate date);
} 