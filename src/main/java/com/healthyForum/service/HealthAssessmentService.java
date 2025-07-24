package com.healthyForum.service;

import com.healthyForum.model.HealthAssessment;
import com.healthyForum.repository.HealthAssessmentRepository;
import com.healthyForum.repository.SleepRepository;
import com.healthyForum.repository.MealRepository;
import com.healthyForum.repository.ExerciseScheduleRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.model.SleepEntry;
import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.ExerciseSchedule;
import com.healthyForum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HealthAssessmentService {

    @Autowired
    private HealthAssessmentRepository assessmentRepository;
    @Autowired
    private SleepRepository sleepRepository;
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private ExerciseScheduleRepository exerciseScheduleRepository;
    @Autowired
    private UserRepository userRepository;

    public HealthAssessment saveAssessment(HealthAssessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public HealthAssessment performAssessment(Long userId) {
        return performAssessment(userId, 7);
    }

    public HealthAssessment performAssessment(Long userId, int window) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found");
        User user = userOpt.get();

        StringBuilder dataWarning = new StringBuilder();
        boolean insufficientData = false;

        // BMI
        Double bmi = null;
        if (user.getHeight() != null && user.getWeight() != null && user.getHeight() > 0) {
            double heightM = user.getHeight() / 100.0;
            bmi = user.getWeight() / (heightM * heightM);
        }

        // --- Sleep Score ---
        var allSleepEntries = sleepRepository.findByUser(user);
        List<SleepEntry> sleepEntries = allSleepEntries.stream()
            .filter(e -> e.getDate() != null && !e.getDate().isBefore(LocalDate.now().minusDays(window)))
            .toList();
        double sleepScore = 0.0;
        if (!sleepEntries.isEmpty()) {
            double totalSleep = 0;
            int count = 0;
            for (SleepEntry entry : sleepEntries) {
                totalSleep += entry.getSleepDurationInHours();
                count++;
            }
            double avgSleep = totalSleep / count;
            if (avgSleep >= 7 && avgSleep <= 9) sleepScore = 8.0;
            else if (avgSleep >= 6 && avgSleep < 7) sleepScore = 6.0;
            else sleepScore = 4.0;
            if (count < 3) { insufficientData = true; dataWarning.append("More sleep data needed. "); }
        } else {
            insufficientData = true; dataWarning.append("No sleep data found. ");
        }

        // --- Nutrition Score ---
        var allMeals = mealRepository.findByUser(user);
        List<MealPlanner> meals = allMeals.stream()
            .filter(m -> m.getMealDate() != null && !m.getMealDate().isBefore(LocalDate.now().minusDays(window)))
            .toList();
        double nutritionScore = 0.0;
        if (!meals.isEmpty()) {
            double totalCalories = 0;
            int count = 0;
            for (MealPlanner meal : meals) {
                if (meal.getMealCalories() != null)
                    totalCalories += meal.getMealCalories().doubleValue();
                count++;
            }
            double avgCalories = totalCalories / count;
            if (avgCalories >= 1800 && avgCalories <= 2200) nutritionScore = 8.0;
            else if ((avgCalories >= 1500 && avgCalories < 1800) || (avgCalories > 2200 && avgCalories <= 2500)) nutritionScore = 6.0;
            else nutritionScore = 4.0;
            if (count < 3) { insufficientData = true; dataWarning.append("More meal data needed. "); }
        } else {
            insufficientData = true; dataWarning.append("No meal data found. ");
        }

        // --- Exercise Score ---
        LocalDate today = LocalDate.now();
        var allSchedules = new java.util.ArrayList<ExerciseSchedule>();
        for (int i = 0; i < window; i++) {
            LocalDate date = today.minusDays(i);
            allSchedules.addAll(exerciseScheduleRepository.findAllByUserAndDateOrderByTimeAsc(user, date));
        }
        List<ExerciseSchedule> schedules = allSchedules.stream()
            .filter(s -> s.getDate() != null && !s.getDate().isBefore(today.minusDays(window)))
            .toList();
        double exerciseScore = 0.0;
        int totalMinutes = 0;
        int count = 0;
        for (ExerciseSchedule sched : schedules) {
            if (sched.getDuration() != null) {
                totalMinutes += sched.getDuration();
                count++;
            }
        }
        if (count > 0) {
            if (totalMinutes >= 150) exerciseScore = 8.0;
            else if (totalMinutes >= 90) exerciseScore = 6.0;
            else exerciseScore = 4.0;
            if (count < 3) { insufficientData = true; dataWarning.append("More exercise data needed. "); }
        } else {
            insufficientData = true; dataWarning.append("No exercise data found. ");
        }

        // --- Overall Score ---
        double sum = 0;
        int n = 0;
        if (bmi != null) { sum += 8.0; n++; }
        if (sleepScore > 0) { sum += sleepScore; n++; }
        if (nutritionScore > 0) { sum += nutritionScore; n++; }
        if (exerciseScore > 0) { sum += exerciseScore; n++; }
        double overallScore = n > 0 ? sum / n : 0.0;

        // --- Risk Level ---
        String riskLevel = "Low";
        if (overallScore < 5.0) riskLevel = "High";
        else if (overallScore < 7.0) riskLevel = "Moderate";

        // --- Recommendations ---
        StringBuilder rec = new StringBuilder();
        // Categorize domains
        class Domain {
            String name; double score; String praise; String tip;
            Domain(String n, double s, String p, String t) { name=n; score=s; praise=p; tip=t; }
        }
        Domain[] domains = new Domain[] {
            new Domain("Sleep", sleepScore, "Great job on your sleep!", "Aim for 7–9 hours per night."),
            new Domain("Nutrition", nutritionScore, "Excellent nutrition habits!", "Balance your meals and aim for 1800–2200 kcal/day."),
            new Domain("Exercise", exerciseScore, "You're staying active!", "Try to get at least 150 minutes of exercise per week."),
            new Domain("BMI", bmi != null && bmi >= 18.5 && bmi <= 24.9 ? 8.0 : 4.0, "Your BMI is in a healthy range!", "Maintain a BMI between 18.5 and 24.9.")
        };
        // Praise strengths
        for (Domain d : domains) {
            if (d.score >= 7.0) rec.append(d.praise).append(" ");
        }
        // Find weaknesses
        Domain worst = null;
        for (Domain d : domains) {
            if (d.score < 7.0) {
                if (worst == null || d.score < worst.score) worst = d;
            }
        }
        if (worst != null) {
            rec.append("However, your ").append(worst.name.toLowerCase()).append(" needs improvement. ").append(worst.tip).append(" ");
            // Add tips for other weak domains
            for (Domain d : domains) {
                if (d != worst && d.score < 7.0) {
                    rec.append(d.tip).append(" ");
                }
            }
        }
        if (rec.length() == 0) rec.append("Keep up the good work!");
        if (insufficientData) rec.append(" Note: ").append(dataWarning);

        HealthAssessment assessment = new HealthAssessment();
        assessment.setUser(user);
        assessment.setAssessmentDate(new java.util.Date());
        assessment.setBmi(bmi);
        assessment.setSleepScore(sleepScore);
        assessment.setNutritionScore(nutritionScore);
        assessment.setExerciseScore(exerciseScore);
        assessment.setOverallScore(overallScore);
        assessment.setRiskLevel(riskLevel);
        assessment.setRecommendations(rec.toString());
        return assessmentRepository.save(assessment);
    }

    public List<HealthAssessment> getUserAssessments(Long userID) {
        return assessmentRepository.findByUser_IdOrderByAssessmentDateDesc(userID);
    }
}