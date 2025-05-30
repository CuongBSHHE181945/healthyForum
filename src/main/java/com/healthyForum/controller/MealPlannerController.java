package com.healthyForum.controller;

import com.healthyForum.model.MealIngredient;
import com.healthyForum.model.MealPlanner;
import com.healthyForum.model.User;
import com.healthyForum.repository.MealRepository;
import com.healthyForum.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/meal-planner")
public class MealPlannerController {

    private final MealRepository mealPlannerRepository;
    private final UserRepository userRepository;

    public MealPlannerController(MealRepository mealPlannerRepository, UserRepository userRepository) {
        this.mealPlannerRepository = mealPlannerRepository;
        this.userRepository = userRepository;
    }

    // Show form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        MealPlanner mealPlanner = new MealPlanner();
        mealPlanner.setMealDate(LocalDate.now());

        List<MealIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MealIngredient ingredient = new MealIngredient();
            ingredient.setMealPlanner(mealPlanner);
            ingredients.add(ingredient);
        }
        mealPlanner.setIngredients(ingredients);

        model.addAttribute("mealPlanner", mealPlanner);
        model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
        model.addAttribute("formAction", "/meal-planner/save"); // ✅ ADD THIS
        return "meal-planner/create";
    }

    // Handle form submission
    @PostMapping("/save")
    public String saveMealPlan(@Valid @ModelAttribute MealPlanner mealPlanner,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
            return "meal-planner/create";
        }

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User with ID 1 not found"));
        mealPlanner.setUser(user); // critical line

// ✅ Remove blank rows
        if (mealPlanner.getIngredients() != null) {
            mealPlanner.getIngredients().removeIf(ing ->
                    (ing.getIngredientName() == null || ing.getIngredientName().isBlank())
                            || ing.getIngredientQuantity() == null);
        }

        // ❗ Reject form if no valid ingredients left
        if (mealPlanner.getIngredients() == null || mealPlanner.getIngredients().isEmpty()) {
            model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
            model.addAttribute("ingredientError", "At least one ingredient is required.");
            return "meal-planner/create";
        }

        for (MealIngredient ingredient : mealPlanner.getIngredients()) {
            ingredient.setMealPlanner(mealPlanner);
        }
        mealPlannerRepository.save(mealPlanner); // Cascades ingredients if set properly
        return "redirect:/meal-planner/view?date=" + mealPlanner.getMealDate();
    }

    // View by date
    @GetMapping("/view")
    public String viewMealsByDate(@RequestParam(value = "date", required = false) LocalDate date, Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MealPlanner> meals = mealPlannerRepository.findByUserAndMealDate(user, date);
        model.addAttribute("selectedDate", date);
        model.addAttribute("meals", meals);
        return "meal-planner/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        MealPlanner meal = mealPlannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        model.addAttribute("mealPlanner", meal);
        model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
        model.addAttribute("formAction", "/meal-planner/update"); // ✅ ADD THIS
        return "meal-planner/create"; // ✅ still reusing create.html
    }

    @PostMapping("/update")
    public String updateMeal(@Valid @ModelAttribute MealPlanner mealPlanner,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
            return "meal-planner/edit";
        }

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        mealPlanner.setUser(user);

        if (mealPlanner.getIngredients() != null) {
            mealPlanner.getIngredients().removeIf(ing ->
                    ing.getIngredientName() == null || ing.getIngredientName().isBlank()
                            || ing.getIngredientQuantity() == null);
        }

        for (MealIngredient ing : mealPlanner.getIngredients()) {
            ing.setMealPlanner(mealPlanner);
        }

        mealPlannerRepository.save(mealPlanner);
        return "redirect:/meal-planner/view?date=" + mealPlanner.getMealDate();
    }

    @PostMapping("/delete/{id}")
    public String deleteMeal(@PathVariable("id") Integer id,
                             @RequestParam("date") String date) {
        mealPlannerRepository.deleteById(id);
        return "redirect:/meal-planner/view?date=" + date;
    }

    @GetMapping("/test-user")
    @ResponseBody
    public String testUserFetch() {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User with ID 1 not found"));

        return "User found: " + user.getUsername();
    }

}
