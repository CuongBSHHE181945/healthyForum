package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "food_item")
public class FoodItem {
    @Id
    @Column(name = "food_id")
    private Integer foodId;

    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbs;
    private BigDecimal fiber;
    private BigDecimal sugar;
    private BigDecimal sodium;
} 