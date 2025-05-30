package com.healthyForum.repository;

import com.healthyForum.model.MealIngredient;
import com.healthyForum.model.MealPlanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealIngredientRepository extends JpaRepository<MealIngredient, Integer> {

    // Get ingredients for a specific meal
    List<MealIngredient> findByMealPlanner(MealPlanner mealPlanner);
}
