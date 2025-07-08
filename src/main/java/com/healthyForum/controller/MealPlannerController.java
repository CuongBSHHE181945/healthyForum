package com.healthyForum.controller;

import com.healthyForum.model.MealIngredient;
import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.User;
import com.healthyForum.repository.MealRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.UserService;
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

@Controller
@RequestMapping("/meal-planner")
public class MealPlannerController {

    private final MealRepository mealPlannerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public MealPlannerController(MealRepository mealPlannerRepository, UserRepository userRepository, UserService userService) {
        this.mealPlannerRepository = mealPlannerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String showMealPlanner(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        List<MealPlanner> userMeals = mealPlannerRepository.findByUser(user);
        model.addAttribute("meals", userMeals);
        model.addAttribute("user", user);
        
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
            existingMeal.setMealCalories(mealPlanner.getMealCalories());

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

}
