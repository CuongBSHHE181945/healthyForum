package com.healthyForum.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "health_assessment")
public class HealthAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // New assessment result fields
    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "sleep_score")
    private Double sleepScore;

    @Column(name = "nutrition_score")
    private Double nutritionScore;

    @Column(name = "exercise_score")
    private Double exerciseScore;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "assessment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assessmentDate = new Date();

    public HealthAssessment() {
    }

    public HealthAssessment(Long id, Double bmi, Double sleepScore, Double nutritionScore, Double exerciseScore, Double overallScore, String riskLevel, String recommendations, User user, Date assessmentDate) {
        this.id = id;
        this.bmi = bmi;
        this.sleepScore = sleepScore;
        this.nutritionScore = nutritionScore;
        this.exerciseScore = exerciseScore;
        this.overallScore = overallScore;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.user = user;
        this.assessmentDate = assessmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(Double sleepScore) {
        this.sleepScore = sleepScore;
    }

    public Double getNutritionScore() {
        return nutritionScore;
    }

    public void setNutritionScore(Double nutritionScore) {
        this.nutritionScore = nutritionScore;
    }

    public Double getExerciseScore() {
        return exerciseScore;
    }

    public void setExerciseScore(Double exerciseScore) {
        this.exerciseScore = exerciseScore;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    // Getters and setters
}