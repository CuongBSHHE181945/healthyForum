package com.healthyForum.repository;

import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface mealRepository extends JpaRepository<User, Long> {
    List<MealPlanner> findByID(int id);
}
