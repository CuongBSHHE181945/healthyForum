package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.challenge.ChallengeService;
import com.healthyForum.service.challenge.ChallengeTrackingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/challenge")
public class TickController {
    private final ChallengeTrackingService challengeTrackingService;
    private final UserChallengeRepository userChallengeRepo;
    private final ChallengeService challengeService;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    public TickController(ChallengeTrackingService challengeTrackingService, UserChallengeRepository userChallengeRepo, ChallengeService challengeService, UserRepository userRepository, UserAccountRepository userAccountRepository) {
        this.challengeTrackingService = challengeTrackingService;
        this.userChallengeRepo = userChallengeRepo;
        this.challengeService = challengeService;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/tick/{id}")
    public String tickToday(@PathVariable("id") int userChallengeId, RedirectAttributes redirectAttributes,
                            Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        boolean success = challengeTrackingService.tickProgress(userChallengeId);
        if (!success) {
            redirectAttributes.addFlashAttribute("tickMessage", "Already ticked today!");
        } else {
            redirectAttributes.addFlashAttribute("tickMessage", "Progress saved for today!");
        }
        // ⏳ Check if completed
        boolean completed = challengeTrackingService.checkAndCompleteIfDone(userChallengeId);

        if (completed) {
            redirectAttributes.addFlashAttribute("tickMessage", "🎉 Challenge completed! You've earned a badge!");
        }

        return "redirect:/challenge/progress/" + userChallengeId;
    }

    @GetMapping("/progress/{id}")
    public String showProgress(@PathVariable("id") int userChallengeId, Model model) {
        UserChallenge uc = userChallengeRepo.findById(userChallengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<UserChallengeProgress> logs = challengeTrackingService.getProgress(userChallengeId);
        int streak = challengeTrackingService.getStreak(userChallengeId);
        int total = logs.size();
        int target = uc.getChallenge().getDurationDays();

        model.addAttribute("userChallenge", uc);
        model.addAttribute("logs", logs);
        model.addAttribute("streak", streak);
        model.addAttribute("total", total);
        model.addAttribute("target", target);
        return "challenge/progress";
    }

    /**
     * Helper method to get current user from Principal (handles both local and OAuth authentication)
     */
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }

        String principalName = principal.getName();
        
        // Try to find by username first using UserAccountRepository
        UserAccount account = userAccountRepository.findByUsername(principalName).orElse(null);
        if (account != null) {
            return account.getUser();
        }

        // Try to find by email (for OAuth authentication)
        User user = userRepository.findByEmail(principalName).orElse(null);
        if (user != null) {
            return user;
        }

        // If principal is OAuth2User, try to get email and find by email
        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                return userRepository.findByEmail(email).orElse(null);
            }
        }

        return null;
    }
}
