package com.healthyForum.service;

import com.healthyForum.model.FoodItem;
import com.healthyForum.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemService {
    @Autowired
    private FoodItemRepository foodItemRepository;

    public List<FoodItem> searchFoods(String query) {
        return foodItemRepository.findByNameContainingIgnoreCase(query);
    }
} 