package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "meal_planner")
public class MealPlanner  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id", nullable = false)
    private Integer mealId;

    @Column(name = "meal_name")
    private String mealName;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "meal_description", columnDefinition = "TEXT")
    private String mealDescription;

    @Column(name = "meal_calories")
    private BigDecimal mealCalories;

    @Column(name = "meal_proteins")
    private BigDecimal mealProteins;

    @Column(name = "meal_carbs")
    private BigDecimal mealCarbs;

    @Column(name = "meal_fats")
    private BigDecimal mealFats;

    @OneToMany(mappedBy = "mealPlanner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> ingredients = new ArrayList<>();

    // 🔗 Link to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}