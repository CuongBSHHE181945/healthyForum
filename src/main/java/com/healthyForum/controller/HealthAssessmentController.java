package com.healthyForum.controller;

import com.healthyForum.model.HealthAssessment;
import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.HealthAssessmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import com.healthyForum.repository.SleepRepository;
import com.healthyForum.repository.MealRepository;
import com.healthyForum.repository.ExerciseScheduleRepository;
import com.healthyForum.model.SleepEntry;
import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.ExerciseSchedule;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/health-assessment")
public class HealthAssessmentController {

    @Autowired
    private HealthAssessmentService assessmentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SleepRepository sleepRepository;
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private ExerciseScheduleRepository exerciseScheduleRepository;

    @GetMapping
    public String showAssessment(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        List<HealthAssessment> assessments = assessmentService.getUserAssessments(user.getId());
        HealthAssessment latest = assessments.isEmpty() ? null : assessments.get(0);
        model.addAttribute("latestAssessment", latest);
        return "health_assessment_form";
    }

    @PostMapping
    public String performAssessment(Model model, Principal principal, @RequestParam(value = "window", required = false, defaultValue = "7") int window) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        try {
            HealthAssessment assessment = assessmentService.performAssessment(user.getId(), window);
            model.addAttribute("result", assessment);

            // Prepare trend data for the selected window
            LocalDate today = LocalDate.now();
            LocalDate start = today.minusDays(window - 1);
            // Sleep trend
            Map<String, Double> sleepMap = new LinkedHashMap<>();
            for (int i = 0; i < window; i++) {
                LocalDate date = start.plusDays(i);
                sleepMap.put(date.toString(), 0.0);
            }
            for (SleepEntry entry : sleepRepository.findByUser(user)) {
                LocalDate date = entry.getDate();
                if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                    sleepMap.put(date.toString(), entry.getSleepDurationInHours());
                }
            }
            model.addAttribute("sleepLabels", sleepMap.keySet());
            model.addAttribute("sleepData", sleepMap.values());

            // Nutrition trend
            Map<String, Double> nutritionMap = new LinkedHashMap<>();
            for (int i = 0; i < window; i++) {
                LocalDate date = start.plusDays(i);
                nutritionMap.put(date.toString(), 0.0);
            }
            for (MealPlanner meal : mealRepository.findByUser(user)) {
                LocalDate date = meal.getMealDate();
                if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                    nutritionMap.put(date.toString(), nutritionMap.get(date.toString()) + (meal.getMealCalories() != null ? meal.getMealCalories().doubleValue() : 0.0));
                }
            }
            model.addAttribute("nutritionLabels", nutritionMap.keySet());
            model.addAttribute("nutritionData", nutritionMap.values());

            // Exercise trend
            Map<String, Double> exerciseMap = new LinkedHashMap<>();
            for (int i = 0; i < window; i++) {
                LocalDate date = start.plusDays(i);
                exerciseMap.put(date.toString(), 0.0);
            }
            for (ExerciseSchedule sched : exerciseScheduleRepository.findAll()) {
                if (sched.getUser() != null && sched.getUser().getId().equals(user.getId())) {
                    LocalDate date = sched.getDate();
                    if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                        exerciseMap.put(date.toString(), exerciseMap.get(date.toString()) + (sched.getDuration() != null ? sched.getDuration() : 0));
                    }
                }
            }
            model.addAttribute("exerciseLabels", exerciseMap.keySet());
            model.addAttribute("exerciseData", exerciseMap.values());

            return "health_assessment_result";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while processing your assessment: " + e.getMessage());
            return "health_assessment_form";
        }
    }

    @GetMapping("/history")
    public String viewHistory(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        List<HealthAssessment> assessments = assessmentService.getUserAssessments(user.getId());
        model.addAttribute("assessments", assessments);
        return "health_assessment_history";
    }

    @GetMapping("/detail/{id}")
    public String viewAssessmentDetail(@PathVariable Long id, Model model, Principal principal, @RequestParam(value = "window", required = false, defaultValue = "7") int window) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        HealthAssessment assessment = assessmentService.getUserAssessments(user.getId()).stream()
                .filter(a -> a.getId().equals(id)).findFirst().orElse(null);
        if (assessment == null) {
            model.addAttribute("error", "Assessment not found or access denied.");
            return "redirect:/health-assessment/history";
        }
        model.addAttribute("result", assessment);

        // Prepare trend data for the selected window
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(window - 1);
        // Sleep trend
        Map<String, Double> sleepMap = new LinkedHashMap<>();
        for (int i = 0; i < window; i++) {
            LocalDate date = start.plusDays(i);
            sleepMap.put(date.toString(), 0.0);
        }
        for (SleepEntry entry : sleepRepository.findByUser(user)) {
            LocalDate date = entry.getDate();
            if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                sleepMap.put(date.toString(), entry.getSleepDurationInHours());
            }
        }
        model.addAttribute("sleepLabels", sleepMap.keySet());
        model.addAttribute("sleepData", sleepMap.values());

        // Nutrition trend
        Map<String, Double> nutritionMap = new LinkedHashMap<>();
        for (int i = 0; i < window; i++) {
            LocalDate date = start.plusDays(i);
            nutritionMap.put(date.toString(), 0.0);
        }
        for (MealPlanner meal : mealRepository.findByUser(user)) {
            LocalDate date = meal.getMealDate();
            if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                nutritionMap.put(date.toString(), nutritionMap.get(date.toString()) + (meal.getMealCalories() != null ? meal.getMealCalories().doubleValue() : 0.0));
            }
        }
        model.addAttribute("nutritionLabels", nutritionMap.keySet());
        model.addAttribute("nutritionData", nutritionMap.values());

        // Exercise trend
        Map<String, Double> exerciseMap = new LinkedHashMap<>();
        for (int i = 0; i < window; i++) {
            LocalDate date = start.plusDays(i);
            exerciseMap.put(date.toString(), 0.0);
        }
        for (ExerciseSchedule sched : exerciseScheduleRepository.findAll()) {
            if (sched.getUser() != null && sched.getUser().getId().equals(user.getId())) {
                LocalDate date = sched.getDate();
                if (date != null && !date.isBefore(start) && !date.isAfter(today)) {
                    exerciseMap.put(date.toString(), exerciseMap.get(date.toString()) + (sched.getDuration() != null ? sched.getDuration() : 0));
                }
            }
        }
        model.addAttribute("exerciseLabels", exerciseMap.keySet());
        model.addAttribute("exerciseData", exerciseMap.values());

        return "health_assessment_result";
    }
}