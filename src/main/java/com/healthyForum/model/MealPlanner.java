package com.healthyForum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "meal_planner")
public class MealPlanner {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "meal_name")
    private String mealName;

    @Column(name="meal_type")
    private String mealType;
}
