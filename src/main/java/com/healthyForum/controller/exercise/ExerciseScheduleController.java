package com.healthyForum.controller.exercise;

import com.healthyForum.model.ExerciseSchedule;
import com.healthyForum.model.User;
import com.healthyForum.service.UserService;
import com.healthyForum.service.exercise.ExerciseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercise/schedule")
public class ExerciseScheduleController {

    @Autowired
    private ExerciseScheduleService exerciseScheduleService;

    @Autowired
    private UserService userService;

    // GET all schedules for the logged-in user for a specific date
    @GetMapping
    public ResponseEntity<List<ExerciseSchedule>> getSchedulesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ExerciseSchedule> schedules = exerciseScheduleService.getSchedulesByUserAndDate(user, date);
        return ResponseEntity.ok(schedules);
    }

    // POST a new exercise schedule
    @PostMapping
    public ResponseEntity<ExerciseSchedule> createSchedule(
            @RequestBody ExerciseSchedule schedule,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ExerciseSchedule createdSchedule = exerciseScheduleService.createSchedule(schedule, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    // PUT (update) an existing exercise schedule
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseSchedule> updateSchedule(
            @PathVariable Long id,
            @RequestBody ExerciseSchedule scheduleDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ExerciseSchedule> updatedSchedule = exerciseScheduleService.updateSchedule(id, scheduleDetails, user);

        return updatedSchedule
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE an exercise schedule
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = exerciseScheduleService.deleteSchedule(id, user);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 