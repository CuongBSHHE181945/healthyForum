package com.healthyForum.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "health_assessment")
public class HealthAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private boolean smoker;

    @Column(name = "exercise_days_per_week", nullable = false)
    private int exerciseDaysPerWeek;

    @Column(name = "sleep_pattern", nullable = false)
    private String sleepPattern;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "health_suggestions", columnDefinition = "TEXT")
    private String healthSuggestions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    private User user;

    @Column(name = "assessment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assessmentDate = new Date();

    public HealthAssessment() {
    }

    public HealthAssessment(Long id, int age, String gender, double height, double weight, boolean smoker, int exerciseDaysPerWeek, String sleepPattern, String riskLevel, String healthSuggestions, User user, Date assessmentDate) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.smoker = smoker;
        this.exerciseDaysPerWeek = exerciseDaysPerWeek;
        this.sleepPattern = sleepPattern;
        this.riskLevel = riskLevel;
        this.healthSuggestions = healthSuggestions;
        this.user = user;
        this.assessmentDate = assessmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }

    public int getExerciseDaysPerWeek() {
        return exerciseDaysPerWeek;
    }

    public void setExerciseDaysPerWeek(int exerciseDaysPerWeek) {
        this.exerciseDaysPerWeek = exerciseDaysPerWeek;
    }

    public String getSleepPattern() {
        return sleepPattern;
    }

    public void setSleepPattern(String sleepPattern) {
        this.sleepPattern = sleepPattern;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getHealthSuggestions() {
        return healthSuggestions;
    }

    public void setHealthSuggestions(String healthSuggestions) {
        this.healthSuggestions = healthSuggestions;
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