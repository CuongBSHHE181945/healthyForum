package com.healthyForum.controller.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.challenge.ChallengeService;
import com.healthyForum.service.challenge.ChallengeTrackingService;
import com.healthyForum.service.UserService;
import com.healthyForum.service.challenge.EvidenceService;
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
import java.util.Optional;

@Controller
@RequestMapping("/challenge")
public class TickController {
    private final ChallengeTrackingService challengeTrackingService;
    private final UserChallengeRepository userChallengeRepo;
    private final ChallengeService challengeService;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final EvidencePostRepository evidencePostRepository;
    private final EvidenceService evidenceService;

    public TickController(ChallengeTrackingService challengeTrackingService, UserChallengeRepository userChallengeRepo, ChallengeService challengeService, UserRepository userRepository, UserAccountRepository userAccountRepository, UserService userService, EvidencePostRepository evidencePostRepository, EvidenceService evidenceService) {
        this.challengeTrackingService = challengeTrackingService;
        this.userChallengeRepo = userChallengeRepo;
        this.challengeService = challengeService;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
        this.userService = userService;
        this.evidencePostRepository = evidencePostRepository;
        this.evidenceService = evidenceService;
    }

//    @PostMapping("/tick/{id}")
//    public String tickToday(@PathVariable("id") int userChallengeId, RedirectAttributes redirectAttributes,
//                            Principal principal) {
//        User user = userService.getCurrentUser(principal);
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }
//
//        boolean success = challengeTrackingService.tickProgress(userChallengeId);
//        if (!success) {
//            redirectAttributes.addFlashAttribute("tickMessage", "Already ticked today!");
//        } else {
//            redirectAttributes.addFlashAttribute("tickMessage", "Progress saved for today!");
//        }
//        // ⏳ Check if completed
//        boolean completed = challengeTrackingService.checkAndCompleteIfDone(userChallengeId);
//
//        if (completed) {
//            redirectAttributes.addFlashAttribute("tickMessage", "🎉 Challenge completed! You've earned a badge!");
//        }
//
//        return "redirect:/challenge/progress/" + userChallengeId;
//    }

    @GetMapping("/progress/{id}")
    public String showProgress(@PathVariable("id") int userChallengeId, Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        List<EvidencePost> evidence = evidencePostRepository.findByUserChallengeId(userChallengeId);

        UserChallenge uc = userChallengeRepo.findById(userChallengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<UserChallengeProgress> logs = challengeTrackingService.getProgress(userChallengeId);
        int streak = challengeTrackingService.getStreak(userChallengeId);
        int total = logs.size();
        int target = uc.getChallenge().getDurationDays();

        model.addAttribute("evidence", evidence);
        model.addAttribute("userChallenge", uc);
        model.addAttribute("logs", logs);
        model.addAttribute("streak", streak);
        model.addAttribute("total", total);
        model.addAttribute("target", target);

        Optional<EvidencePost> todayEvidence = evidenceService.getLatestEvidence(userChallengeId);
        String evidenceAction = "upload"; // default

        if (todayEvidence.isPresent()) {
            EvidenceStatus status = todayEvidence.get().getStatus(); // PENDING, APPROVED, REJECTED
            if (status == EvidenceStatus.REJECTED) {
                evidenceAction = "retry";
            } else {
                evidenceAction = "edit";
            }
        }

        model.addAttribute("evidenceAction", evidenceAction);
        model.addAttribute("todayEvidence", todayEvidence.orElse(null));

        return "challenge/progress";
    }
}
