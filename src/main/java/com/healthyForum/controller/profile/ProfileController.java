package com.healthyForum.controller.profile;

import com.healthyForum.model.HealthAssessment;
import com.healthyForum.model.User;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.HealthAssessmentRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final HealthAssessmentRepository healthAssessmentRepository;

    public ProfileController(UserRepository userRepository, BadgeService badgeService, UserBadgeService userBadgeService, HealthAssessmentRepository healthAssessmentRepository) {
        this.userRepository = userRepository;
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
        this.healthAssessmentRepository = healthAssessmentRepository;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String principalName = principal.getName();
        User user = userRepository.findByUsername(principalName)
                .or(() -> userRepository.findByGoogleId(principalName))
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database: " + principalName));

        // Fetch the latest health assessment
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_UserIDOrderByAssessmentDateDesc(user.getUserID());
        HealthAssessment latestAssessment = assessments.stream().findFirst().orElse(null);

        List<UserBadge> allUserBadges = userBadgeService.getAllUnlockedByUser(user.getUserID());
        List<Badge> unlockedBadges = new ArrayList<>();
        List<Badge> lockedBadges = badgeService.getAllBadges(); // clone to remove unlocked

        List<Badge> displayedBadges = new ArrayList<>();

        for (UserBadge ub : allUserBadges) {
            Badge badge = ub.getBadge();
            unlockedBadges.add(badge);
            lockedBadges.remove(badge);
            if (ub.isDisplayed()) {
                displayedBadges.add(badge);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("healthAssessment", latestAssessment);
        model.addAttribute("displayedBadges", displayedBadges);
        model.addAttribute("unlockedBadges", unlockedBadges);
        model.addAttribute("lockedBadges", lockedBadges);

        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String principalName = principal.getName();
        User user = userRepository.findByUsername(principalName)
                .or(() -> userRepository.findByGoogleId(principalName))
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database: " + principalName));
        model.addAttribute("user", user);
        // Add latest health assessment to the model
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_UserIDOrderByAssessmentDateDesc(user.getUserID());
        HealthAssessment latestAssessment = assessments.stream().findFirst().orElse(null);
        model.addAttribute("healthAssessment", latestAssessment);
        return "profile/edit-profile";
    }

    @PostMapping("/edit")
    public String editProfileSubmit(@ModelAttribute("user") User formUser,
                                    @RequestParam(value = "height", required = false) Double height,
                                    @RequestParam(value = "weight", required = false) Double weight,
                                    Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String principalName = principal.getName();
        User user = userRepository.findByUsername(principalName)
                .or(() -> userRepository.findByGoogleId(principalName))
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database: " + principalName));
        // Only allow editing of certain fields
        if (!formUser.getFullname().matches("^[A-Za-z\\s]+$")) {
            redirectAttributes.addFlashAttribute("error", "Full name must contain only letters and spaces.");
            return "redirect:/profile/edit";
        }
        user.setFullname(formUser.getFullname());
        if (formUser.getDob() != null) {
            user.setDob(formUser.getDob());
        }
        user.setGender(formUser.getGender());
        user.setAddress(formUser.getAddress());
        userRepository.save(user);
        // Handle height/weight
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_UserIDOrderByAssessmentDateDesc(user.getUserID());
        HealthAssessment latestAssessment = assessments.stream().findFirst().orElse(null);
        if (height != null || weight != null) {
            if (latestAssessment == null) {
                latestAssessment = new HealthAssessment();
                latestAssessment.setUser(user);
                latestAssessment.setAssessmentDate(new java.util.Date());
                latestAssessment.setAge(0); // or calculate if you want
                latestAssessment.setGender(user.getGender() != null ? user.getGender() : "");
                latestAssessment.setHeight(height != null ? height : 0);
                latestAssessment.setWeight(weight != null ? weight : 0);
                latestAssessment.setSmoker(false);
                latestAssessment.setExerciseDaysPerWeek(0);
                latestAssessment.setSleepPattern("");
                healthAssessmentRepository.save(latestAssessment);
            } else {
                if (height != null) latestAssessment.setHeight(height);
                if (weight != null) latestAssessment.setWeight(weight);
                healthAssessmentRepository.save(latestAssessment);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
    }

//    @PostMapping("display-badge")
//    @ResponseBody
//    public ResponseEntity<?> addDisplayedBadge(@RequestParam int badgeId, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
//        userBadgeService.setBadgeDisplayed(user.getUserID(), badgeId);
//        return ResponseEntity.ok().build();
//    }
}
