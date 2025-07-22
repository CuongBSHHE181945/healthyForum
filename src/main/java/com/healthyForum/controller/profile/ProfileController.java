package com.healthyForum.controller.profile;

import com.healthyForum.model.Follow;
import com.healthyForum.model.HealthAssessment;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.FollowRepository;
import com.healthyForum.repository.HealthAssessmentRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.UserAccountService;
import com.healthyForum.service.HealthAssessmentService;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private HealthAssessmentService healthAssessmentService;

    @Autowired
    private FollowRepository followRepository;

    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final HealthAssessmentRepository healthAssessmentRepository;

    public ProfileController(BadgeService badgeService, UserBadgeService userBadgeService, HealthAssessmentRepository healthAssessmentRepository) {
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
        UserAccount account = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
        if (account == null) {
            throw new IllegalArgumentException("Authenticated user not found in database: " + principalName);
        }
        
        // Refresh user data from database to get latest updates
        User user = userRepository.findById(account.getUser().getId()).orElse(account.getUser());
        User viewer = account.getUser();
        
        // Calculate age if it's null but DOB exists
        if (user.getAge() == null && user.getDob() != null) {
            user.setAge(user.calculateAge());
            userRepository.save(user);
        }
        
        System.out.println("[DEBUG] viewProfile: User data - fullName: " + user.getFullName() + ", height: " + user.getHeight() + ", weight: " + user.getWeight() + ", age: " + user.getAge());

        // Fetch the latest health assessment
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_IdOrderByAssessmentDateDesc(user.getId());
        HealthAssessment latestAssessment = assessments.stream().findFirst().orElse(null);

        List<UserBadge> allUserBadges = userBadgeService.getAllUnlockedByUser(user.getId());
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

        List<Follow> followers = followRepository.findByFollowed(user);
        List<Follow> following = followRepository.findByFollower(user);

        model.addAttribute("user", user);
        model.addAttribute("isOwner", viewer.getId().equals(user.getId()));
        model.addAttribute("healthAssessment", latestAssessment);
        model.addAttribute("displayedBadges", displayedBadges);
        model.addAttribute("unlockedBadges", unlockedBadges);
        model.addAttribute("lockedBadges", lockedBadges);
        model.addAttribute("followers", followers);
        model.addAttribute("followerCount", followers.size());
        model.addAttribute("followingCount", following.size());

        return "profile/profile";
    }

    @GetMapping("/{id}")
    public String viewUserProfile(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String principalName = principal.getName();
        UserAccount viewerAccount = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));

        if (viewerAccount == null) throw new IllegalArgumentException("User not found: " + principalName);

        Optional<User> targetUserOpt = userRepository.findById(id);
        if (targetUserOpt.isEmpty()) return "redirect:/error";

        User targetUser = targetUserOpt.get();
        User viewer = viewerAccount.getUser();

        if (targetUser.getAge() == null && targetUser.getDob() != null) {
            targetUser.setAge(targetUser.calculateAge());
            userRepository.save(targetUser);
        }


        // Calculate age if it's null but DOB exists
        if (targetUser.getAge() == null && targetUser.getDob() != null) {
            targetUser.setAge(targetUser.calculateAge());
            userRepository.save(targetUser);
        }

        System.out.println("[DEBUG] viewProfile: User data - fullName: " + targetUser.getFullName() + ", height: " + targetUser.getHeight() + ", weight: " + targetUser.getWeight() + ", age: " + targetUser.getAge());

        // Fetch the latest health assessment
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_IdOrderByAssessmentDateDesc(targetUser.getId());
        HealthAssessment latestAssessment = assessments.stream().findFirst().orElse(null);

        List<UserBadge> allUserBadges = userBadgeService.getAllUnlockedByUser(targetUser.getId());
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

        boolean isOwner = viewer.getId().equals(targetUser.getId());
        boolean isFollowing = followRepository.existsByFollowerAndFollowed(viewer, targetUser);
        long followerCount = followRepository.countByFollowed(targetUser);
        long followingCount = followRepository.countByFollower(targetUser);

        List<Follow> followers = followRepository.findByFollowed(targetUser);
        List<Follow> following = followRepository.findByFollower(targetUser);

        model.addAttribute("user", targetUser);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("isOwner", viewer.getId().equals(targetUser.getId()));
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
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
        UserAccount account = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
        if (account == null) {
            throw new IllegalArgumentException("Authenticated user not found in database: " + principalName);
        }
        
        // Refresh user data from database to get latest updates
        User user = userRepository.findById(account.getUser().getId()).orElse(account.getUser());
        
        // Calculate age if it's null but DOB exists
        if (user.getAge() == null && user.getDob() != null) {
            user.setAge(user.calculateAge());
            userRepository.save(user);
        }
        
        model.addAttribute("user", user);
        // Add latest health assessment to the model
        List<HealthAssessment> assessments = healthAssessmentRepository.findByUser_IdOrderByAssessmentDateDesc(user.getId());
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
        UserAccount account = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
        if (account == null) {
            throw new IllegalArgumentException("Authenticated user not found in database: " + principalName);
        }
        User user = account.getUser();
        
        System.out.println("[DEBUG] editProfileSubmit: Updating profile for user: " + user.getFullName());
        System.out.println("[DEBUG] editProfileSubmit: Form data - fullName: " + formUser.getFullName() + ", height: " + height + ", weight: " + weight);
        
        // Only allow editing of certain fields
        if (!formUser.getFullName().matches("^[A-Za-z\\s]+$")) {
            redirectAttributes.addFlashAttribute("error", "Full name must contain only letters and spaces.");
            return "redirect:/profile/edit";
        }
        user.setFullName(formUser.getFullName());
        if (formUser.getDob() != null) {
            user.setDob(formUser.getDob());
            // Calculate and set age when DOB is updated
            user.setAge(user.calculateAge());
        }
        user.setGender(formUser.getGender());
        user.setAddress(formUser.getAddress());
        
        // Update health fields directly in User model
        if (height != null) {
            user.setHeight(height);
        }
        if (weight != null) {
            user.setWeight(weight);
        }
        
        System.out.println("[DEBUG] editProfileSubmit: Saving user with updated data - fullName: " + user.getFullName() + ", height: " + user.getHeight() + ", weight: " + user.getWeight() + ", age: " + user.getAge());
        
        userRepository.save(user);
        
        System.out.println("[DEBUG] editProfileSubmit: User saved successfully");
        
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/exercise-scheduler")
    public String viewExerciseScheduler(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "exercise/scheduler";
    }

//    @PostMapping("display-badge")
//    @ResponseBody
//    public ResponseEntity<?> addDisplayedBadge(@RequestParam int badgeId, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
//        userBadgeService.setBadgeDisplayed(user.getId(), badgeId);
//        return ResponseEntity.ok().build();
//    }
}
