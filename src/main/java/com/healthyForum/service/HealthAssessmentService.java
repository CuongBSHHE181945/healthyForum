package com.healthyForum.service;

import com.healthyForum.model.HealthAssessment;
import com.healthyForum.repository.HealthAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthAssessmentService {

    @Autowired
    private HealthAssessmentRepository assessmentRepository;

    public HealthAssessment saveAssessment(HealthAssessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public void evaluateRiskAndSuggestions(HealthAssessment assessment) {
        int riskScore = calculateRiskScore(assessment);
        setRiskLevelAndSuggestions(assessment, riskScore);
    }


    public List<HealthAssessment> getUserAssessments(Long userID) {
        return assessmentRepository.findByUser_IdOrderByAssessmentDateDesc(userID);
    }


    private int calculateRiskScore(HealthAssessment assessment) {
        int score = 0;

        // Age risk
        if (assessment.getAge() > 50) score += 2;
        else if (assessment.getAge() > 35) score += 1;

        // Exercise risk (based on days per week)
        if (assessment.getExerciseDaysPerWeek() < 2) score += 2;
        else if (assessment.getExerciseDaysPerWeek() < 4) score += 1;

        // Smoking risk
        if (assessment.isSmoker()) score += 3;

        // Sleep pattern risk
        if ("Poor".equals(assessment.getSleepPattern())) score += 2;
        else if ("Fair".equals(assessment.getSleepPattern())) score += 1;

        return score;
    }

    private void setRiskLevelAndSuggestions(HealthAssessment assessment, int riskScore) {
        if (riskScore >= 5) {
            assessment.setRiskLevel("High");
            assessment.setHealthSuggestions("Immediate lifestyle changes recommended. Try to exercise at least 3-4 days per week, quit smoking, and improve sleep habits.");
        } else if (riskScore >= 3) {
            assessment.setRiskLevel("Moderate");
            assessment.setHealthSuggestions("Some lifestyle improvements needed. Consider increasing exercise to 4-5 days per week and maintaining better sleep patterns.");
        } else {
            assessment.setRiskLevel("Low");
            assessment.setHealthSuggestions("Keep maintaining your healthy lifestyle!");
        }
    }

}