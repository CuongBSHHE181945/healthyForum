package com.healthyForum.controller;

import com.healthyForum.model.MealIngredient;
import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.User;
import com.healthyForum.model.FoodItem;
import com.healthyForum.repository.MealRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;

@Controller
@RequestMapping("/meal-planner")
public class MealPlannerController {

    private final MealRepository mealPlannerRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FoodItemService foodItemService;

    public MealPlannerController(MealRepository mealPlannerRepository, UserRepository userRepository, UserService userService, FoodItemService foodItemService) {
        this.mealPlannerRepository = mealPlannerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.foodItemService = foodItemService;
    }

    @GetMapping("")
    public String showMealPlanner(@RequestParam(value = "date", required = false) String dateStr, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        List<MealPlanner> userMeals;
        LocalDate selectedDate = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            selectedDate = LocalDate.parse(dateStr);
            userMeals = mealPlannerRepository.findByUserAndMealDate(user, selectedDate);
        } else {
            userMeals = mealPlannerRepository.findByUser(user);
        }
        model.addAttribute("meals", userMeals);
        model.addAttribute("user", user);
        model.addAttribute("selectedDate", selectedDate != null ? selectedDate : "");

        return "meal-planner/view";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("mealPlanner", new MealPlanner());
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/meal-planner/create");
        model.addAttribute("mealTypes", getMealTypes());
        
        return "meal-planner/create";
    }

    @PostMapping("/create")
    public String createMeal(@ModelAttribute MealPlanner mealPlanner, 
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            mealPlanner.setUser(user);

            // Defensive: Remove null/empty ingredients
            if (mealPlanner.getIngredients() != null) {
                mealPlanner.getIngredients().removeIf(ing -> ing.getIngredientName() == null || ing.getIngredientName().trim().isEmpty());
                for (MealIngredient ingredient : mealPlanner.getIngredients()) {
                    ingredient.setMealPlanner(mealPlanner);
                }
            }

            // Calculate nutrition totals from ingredients
            calculateNutritionFromIngredients(mealPlanner);

            mealPlannerRepository.save(mealPlanner);
            redirectAttributes.addFlashAttribute("success", "Meal plan created successfully!");
            return "redirect:/meal-planner";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create meal plan: " + e.getMessage());
            return "redirect:/meal-planner/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        MealPlanner meal = mealPlannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        if (meal == null || !meal.getUser().getId().equals(user.getId())) {
            return "redirect:/meal-planner";
        }

        model.addAttribute("mealPlanner", meal);
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/meal-planner/edit/" + id);
        model.addAttribute("mealTypes", getMealTypes());
        
        return "meal-planner/create"; // Reuse create form for editing
    }

    @PostMapping("/edit/{id}")
    public String updateMeal(@PathVariable Integer id, 
                             @ModelAttribute MealPlanner mealPlanner, 
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            MealPlanner existingMeal = mealPlannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Meal not found"));
            if (existingMeal == null || !existingMeal.getUser().getId().equals(user.getId())) {
                return "redirect:/meal-planner";
            }

            existingMeal.setMealName(mealPlanner.getMealName());
            existingMeal.setMealType(mealPlanner.getMealType());
            existingMeal.setMealDate(mealPlanner.getMealDate());
            existingMeal.setMealDescription(mealPlanner.getMealDescription());

            // Robustly update ingredients: clear and repopulate
            existingMeal.getIngredients().clear();
            if (mealPlanner.getIngredients() != null) {
                mealPlanner.getIngredients().removeIf(ing -> ing.getIngredientName() == null || ing.getIngredientName().trim().isEmpty());
                for (MealIngredient ingredient : mealPlanner.getIngredients()) {
                    ingredient.setMealPlanner(existingMeal);
                    existingMeal.getIngredients().add(ingredient);
                }
            }

            // Calculate nutrition totals from ingredients
            calculateNutritionFromIngredients(existingMeal);

            mealPlannerRepository.save(existingMeal);
            redirectAttributes.addFlashAttribute("success", "Meal plan updated successfully!");
            return "redirect:/meal-planner";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update meal plan: " + e.getMessage());
            return "redirect:/meal-planner/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMeal(@PathVariable Integer id, 
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            MealPlanner meal = mealPlannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Meal not found"));
            if (meal == null || !meal.getUser().getId().equals(user.getId())) {
                return "redirect:/meal-planner";
            }

            // Orphan removal will delete ingredients
            mealPlannerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Meal plan deleted successfully!");
            return "redirect:/meal-planner";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete meal plan: " + e.getMessage());
            return "redirect:/meal-planner";
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public String testUser(Principal principal) {
        if (principal == null) {
            return "No principal";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "User not found";
        }

        return "User found: " + user.getEmail();
    }

    @GetMapping("/food-search")
    @ResponseBody
    public ResponseEntity<List<FoodItem>> searchFood(@RequestParam("q") String query) {
        List<FoodItem> results = foodItemService.searchFoods(query);
        return ResponseEntity.ok(results);
    }

    private List<String> getMealTypes() {
        return List.of("Breakfast", "Lunch", "Dinner", "Snack");
    }

    private void calculateNutritionFromIngredients(MealPlanner meal) {
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFiber = BigDecimal.ZERO;
        BigDecimal totalSugar = BigDecimal.ZERO;
        BigDecimal totalSodium = BigDecimal.ZERO;

        if (meal.getIngredients() != null) {
            for (MealIngredient ingredient : meal.getIngredients()) {
                // Get nutrition data from food database
                List<FoodItem> foodItems = foodItemService.searchFoods(ingredient.getIngredientName());
                if (!foodItems.isEmpty()) {
                    FoodItem food = foodItems.get(0); // Use first match
                    BigDecimal quantity = BigDecimal.valueOf(ingredient.getIngredientQuantity());
                    String unit = ingredient.getIngredientUnit();
                    
                    // Convert to grams for calculation
                    BigDecimal quantityInGrams = convertToGrams(quantity, unit, ingredient.getIngredientName());
                    
                    // Calculate nutrition (assuming food database values are per 100g)
                    BigDecimal multiplier = quantityInGrams.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
                    
                    // Add nutrition values
                    if (food.getCalories() != null) {
                        totalCalories = totalCalories.add(food.getCalories().multiply(multiplier));
                    }
                    if (food.getProtein() != null) {
                        totalProtein = totalProtein.add(food.getProtein().multiply(multiplier));
                    }
                    if (food.getFat() != null) {
                        totalFat = totalFat.add(food.getFat().multiply(multiplier));
                    }
                    if (food.getCarbs() != null) {
                        totalCarbs = totalCarbs.add(food.getCarbs().multiply(multiplier));
                    }
                    if (food.getFiber() != null) {
                        totalFiber = totalFiber.add(food.getFiber().multiply(multiplier));
                    }
                    if (food.getSugar() != null) {
                        totalSugar = totalSugar.add(food.getSugar().multiply(multiplier));
                    }
                    if (food.getSodium() != null) {
                        totalSodium = totalSodium.add(food.getSodium().multiply(multiplier));
                    }
                }
            }
        }

        meal.setMealCalories(totalCalories);
        meal.setMealProteins(totalProtein);
        meal.setMealFats(totalFat);
        meal.setMealCarbs(totalCarbs);
        // Note: You may need to add fiber, sugar, sodium fields to MealPlanner model if they don't exist
    }

    private BigDecimal convertToGrams(BigDecimal quantity, String unit, String foodName) {
        if (unit == null || unit.isEmpty()) {
            return quantity; // Assume grams if no unit specified
        }
        
        switch (unit.toLowerCase()) {
            case "grams":
            case "g":
                return quantity;
            case "pieces":
            case "whole":
                return convertPiecesToGrams(quantity, foodName);
            case "cups":
                return quantity.multiply(BigDecimal.valueOf(240)); // 1 cup ≈ 240g
            case "tablespoons":
            case "tbsp":
                return quantity.multiply(BigDecimal.valueOf(15)); // 1 tbsp ≈ 15g
            case "teaspoons":
            case "tsp":
                return quantity.multiply(BigDecimal.valueOf(5)); // 1 tsp ≈ 5g
            case "milliliters":
            case "ml":
                return quantity; // 1ml ≈ 1g for most foods
            case "ounces":
            case "oz":
                return quantity.multiply(BigDecimal.valueOf(28.35)); // 1 oz ≈ 28.35g
            case "pounds":
            case "lb":
                return quantity.multiply(BigDecimal.valueOf(453.59)); // 1 lb ≈ 453.59g
            case "slices":
                return convertSlicesToGrams(quantity, foodName);
            default:
                return quantity; // Default to grams
        }
    }

    private BigDecimal convertPiecesToGrams(BigDecimal quantity, String foodName) {
        // Common food weights per piece
        String lowerName = foodName.toLowerCase();
        if (lowerName.contains("banana")) {
            return quantity.multiply(BigDecimal.valueOf(120)); // 1 banana ≈ 120g
        } else if (lowerName.contains("apple")) {
            return quantity.multiply(BigDecimal.valueOf(182)); // 1 apple ≈ 182g
        } else if (lowerName.contains("orange")) {
            return quantity.multiply(BigDecimal.valueOf(131)); // 1 orange ≈ 131g
        } else if (lowerName.contains("egg")) {
            return quantity.multiply(BigDecimal.valueOf(50)); // 1 egg ≈ 50g
        } else if (lowerName.contains("bread")) {
            return quantity.multiply(BigDecimal.valueOf(30)); // 1 slice bread ≈ 30g
        } else {
            return quantity.multiply(BigDecimal.valueOf(100)); // Default: 100g per piece
        }
    }

    private BigDecimal convertSlicesToGrams(BigDecimal quantity, String foodName) {
        // Common food weights per slice
        String lowerName = foodName.toLowerCase();
        if (lowerName.contains("bread")) {
            return quantity.multiply(BigDecimal.valueOf(30)); // 1 slice bread ≈ 30g
        } else if (lowerName.contains("cheese")) {
            return quantity.multiply(BigDecimal.valueOf(28)); // 1 slice cheese ≈ 28g
        } else if (lowerName.contains("tomato")) {
            return quantity.multiply(BigDecimal.valueOf(20)); // 1 slice tomato ≈ 20g
        } else {
            return quantity.multiply(BigDecimal.valueOf(25)); // Default: 25g per slice
        }
    }
}
