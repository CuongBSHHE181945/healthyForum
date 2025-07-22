package com.healthyForum.service.exercise;

import com.healthyForum.model.ExerciseSchedule;
import com.healthyForum.model.User;
import com.healthyForum.repository.ExerciseScheduleRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExerciseScheduleService {

    @Autowired
    private ExerciseScheduleRepository exerciseScheduleRepository;

    @Autowired
    private UserRepository userRepository;

    // MET (Metabolic Equivalent of Task) values for various activities.
    // In a real-world application, this might come from a database or configuration file.
    private static final Map<String, Double> MET_VALUES = Map.ofEntries(
            Map.entry("Walking-Low", 2.0),
            Map.entry("Walking-Medium", 3.5),
            Map.entry("Walking-High", 4.5),
            Map.entry("Running-Low", 6.0),
            Map.entry("Running-Medium", 8.0),
            Map.entry("Running-High", 11.5),
            Map.entry("Yoga-Low", 2.0),
            Map.entry("Yoga-Medium", 2.5),
            Map.entry("Yoga-High", 4.0),
            Map.entry("Weight Lifting-Medium", 3.5),
            Map.entry("Weight Lifting-High", 6.0),
            Map.entry("Cycling-Low", 4.0),
            Map.entry("Cycling-Medium", 8.0),
            Map.entry("Cycling-High", 10.0),
            // New types
            Map.entry("Swimming-Low", 6.0),
            Map.entry("Swimming-Medium", 8.0),
            Map.entry("Swimming-High", 10.0),
            Map.entry("HIIT-Medium", 8.0),
            Map.entry("HIIT-High", 12.0),
            Map.entry("Dancing-Low", 3.0),
            Map.entry("Dancing-Medium", 5.0),
            Map.entry("Dancing-High", 7.0),
            Map.entry("Hiking-Medium", 6.0),
            Map.entry("Hiking-High", 7.5),
            Map.entry("Pilates-Low", 3.0),
            Map.entry("Pilates-Medium", 4.0),
            Map.entry("Rowing-Medium", 7.0),
            Map.entry("Rowing-High", 12.0),
            Map.entry("Elliptical-Medium", 5.0),
            Map.entry("Elliptical-High", 8.0),
            Map.entry("Basketball-Medium", 6.5),
            Map.entry("Basketball-High", 8.0),
            Map.entry("Soccer-Medium", 7.0),
            Map.entry("Soccer-High", 10.0),
            Map.entry("Tennis-Medium", 7.0),
            Map.entry("Tennis-High", 9.0)
    );

    /**
     * Get all exercise schedules for a user on a specific date.
     */
    public List<ExerciseSchedule> getSchedulesByUserAndDate(User user, LocalDate date) {
        return exerciseScheduleRepository.findAllByUserAndDateOrderByTimeAsc(user, date);
    }

    /**
     * Create a new exercise schedule.
     */
    public ExerciseSchedule createSchedule(ExerciseSchedule schedule, User user) {
        schedule.setUser(user);
        if (schedule.isCaloriesEstimated()) {
            estimateCalories(schedule);
        }
        // else: use the manually entered calories value
        return exerciseScheduleRepository.save(schedule);
    }

    /**
     * Update an existing exercise schedule.
     */
    public Optional<ExerciseSchedule> updateSchedule(Long id, ExerciseSchedule updatedSchedule, User user) {
        return exerciseScheduleRepository.findById(id)
                .map(existingSchedule -> {
                    if (!existingSchedule.getUser().getId().equals(user.getId())) {
                        // Or throw an exception
                        return null; 
                    }
                    existingSchedule.setDate(updatedSchedule.getDate());
                    existingSchedule.setTime(updatedSchedule.getTime());
                    existingSchedule.setType(updatedSchedule.getType());
                    existingSchedule.setName(updatedSchedule.getName());
                    existingSchedule.setDuration(updatedSchedule.getDuration());
                    existingSchedule.setIntensity(updatedSchedule.getIntensity());
                    existingSchedule.setNotes(updatedSchedule.getNotes());

                    // If calories were manually set, don't re-estimate unless key fields changed
                    if (updatedSchedule.isCaloriesEstimated()) {
                        estimateCalories(existingSchedule);
                    } else {
                        existingSchedule.setCalories(updatedSchedule.getCalories());
                        existingSchedule.setCaloriesEstimated(false);
                    }
                    
                    return exerciseScheduleRepository.save(existingSchedule);
                });
    }

    /**
     * Delete an exercise schedule.
     */
    public boolean deleteSchedule(Long id, User user) {
        return exerciseScheduleRepository.findById(id)
                .map(schedule -> {
                    if (!schedule.getUser().getId().equals(user.getId())) {
                        return false;
                    }
                    exerciseScheduleRepository.delete(schedule);
                    return true;
                }).orElse(false);
    }

    /**
     * Estimates the calories burned for a given exercise schedule based on MET values.
     */
    private void estimateCalories(ExerciseSchedule schedule) {
        if (schedule.getUser() == null || schedule.getUser().getWeight() == null || schedule.getDuration() == null) {
            schedule.setCalories(0);
            return;
        }

        String metKey = schedule.getType() + "-" + schedule.getIntensity();
        double metValue = MET_VALUES.getOrDefault(metKey, 0.0);
        double weightInKg = schedule.getUser().getWeight();
        double durationInMinutes = schedule.getDuration();

        if (metValue == 0.0) {
            schedule.setCalories(0); // Cannot calculate if MET value is unknown
            return;
        }
        
        // Formula: Calories/min = (MET * 3.5 * weightInKg) / 200
        double caloriesPerMinute = (metValue * 3.5 * weightInKg) / 200;
        int estimatedCalories = (int) Math.round(caloriesPerMinute * durationInMinutes);

        schedule.setCalories(estimatedCalories);
        schedule.setCaloriesEstimated(true);
    }
} 