package com.healthyForum.repository;

import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<MealPlanner, Integer> {
    List<MealPlanner> findByUser(User user);
    List<MealPlanner> findByUserAndMealDate(User user, LocalDate mealDate);
}
