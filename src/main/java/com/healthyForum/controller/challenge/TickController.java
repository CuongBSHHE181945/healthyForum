package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.challenge.ChallengeService;
import com.healthyForum.service.challenge.ChallengeTrackingService;
import org.springframework.http.HttpStatus;
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

    public TickController(ChallengeTrackingService challengeTrackingService, UserChallengeRepository userChallengeRepo, ChallengeService challengeService, UserRepository userRepository) {
        this.challengeTrackingService = challengeTrackingService;
        this.userChallengeRepo = userChallengeRepo;
        this.challengeService = challengeService;
        this.userRepository = userRepository;
    }

    @PostMapping("/tick/{id}")
    public String tickToday(@PathVariable("id") int userChallengeId, RedirectAttributes redirectAttributes,
                            Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


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
}
