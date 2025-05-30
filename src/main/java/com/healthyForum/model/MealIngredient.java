package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "meal_ingredient")
public class MealIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Integer ingredientId;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(name = "ingredient_quantity", nullable = false)
    private Integer ingredientQuantity;

    @Column(name = "ingredient_unit")
    private String ingredientUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private MealPlanner mealPlanner;

}
