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


@Controller
@RequestMapping("/health-assessment")
public class HealthAssessmentController {

    @Autowired
    private HealthAssessmentService assessmentService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showAssessmentForm(Model model) {
        model.addAttribute("assessment", new HealthAssessment());
        return "health_assessment_form";
    }

    @PostMapping
    public String processAssessment(@Valid @ModelAttribute("assessment") HealthAssessment assessment,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "health_assessment_form";
        }

        try {
            assessment.setAssessmentDate(new Date());

            // Get real user from repository (for demo using ID 1)
            User user = userRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            assessment.setUser(user);

            assessmentService.evaluateRiskAndSuggestions(assessment);
            HealthAssessment savedAssessment = assessmentService.saveAssessment(assessment);

            model.addAttribute("result", savedAssessment);
            return "health_assessment_result";

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while processing your assessment");
            return "health_assessment_form";
        }
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        try {
            Long userId = 1L;
            List<HealthAssessment> assessments = assessmentService.getUserAssessments(userId);
            model.addAttribute("assessments", assessments);
            return "health_assessment_history";

        } catch (Exception e) {
            model.addAttribute("error", "Unable to retrieve assessment history");
            return "redirect:/health-assessment";
        }
    }
}