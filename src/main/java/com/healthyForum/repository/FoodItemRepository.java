package com.healthyForum.repository;

import com.healthyForum.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {
    List<FoodItem> findByNameContainingIgnoreCase(String name);
} 