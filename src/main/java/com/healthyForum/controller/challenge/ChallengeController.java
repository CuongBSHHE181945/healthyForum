package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.model.challenge.ChallengeCategory;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.ChallengeCategoryRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.challenge.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final BadgeService badgeService;

    public ChallengeController(ChallengeService challengeService, UserRepository userRepository, UserAccountRepository userAccountRepository, UserService userService, ChallengeCategoryRepository challengeCategoryRepository, BadgeService badgeService) {
        this.challengeService = challengeService;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
        this.userService = userService;
        this.challengeCategoryRepository = challengeCategoryRepository;
        this.badgeService = badgeService;
    }

    @GetMapping
    public String showChallengeList(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Challenge> allChallenges = challengeService.getAllChallenges();
        List<Integer> joinedChallengeIds = challengeService.getJoinedChallengeIds(user);
        List<Integer> badgeChallengeIds = challengeService.getBadgeEarnedChallengeIds(user);

        model.addAttribute("challenges", allChallenges);
        model.addAttribute("joinedIds", joinedChallengeIds);
        model.addAttribute("badgeEarnedIds", badgeChallengeIds);
        return "challenge/list";
    }

    @GetMapping("/my")
    public String showMyChallenges(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<UserChallenge> active = challengeService.getActiveChallengesForUser(user);
        model.addAttribute("myChallenges", active);
        return "challenge/my-challenge";
    }

    @GetMapping("/{id}")
    public String showChallengeDetail(@PathVariable int id, Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        Challenge challenge = challengeService.getChallengeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("challenge", challenge);
        List<Integer> joinedChallengeIds = challengeService.getJoinedChallengeIds(user);
        model.addAttribute("joinedIds", joinedChallengeIds);
        return "challenge/detail";
    }

    @PostMapping("/join/{id}")
    public String joinChallenge(@PathVariable int id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        challengeService.joinChallenge(user, id);
        boolean alreadyEarned = challengeService.hasUserEarnedBadge(user.getId(), id);
        if (alreadyEarned) {
            redirectAttributes.addFlashAttribute("warning", "You've already earned the badge for this challenge, but feel free to rejoin!");
        } else {
            redirectAttributes.addFlashAttribute("success", "Challenge joined successfully!");
        }
        return "redirect:/challenge";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Challenge challenge = new Challenge();
        List<ChallengeCategory> categories = challengeCategoryRepository.findAll();

        model.addAttribute("challenge", challenge);
        model.addAttribute("badge", new Badge());
        model.addAttribute("categories", categories);
        return "challenge/create";
    }

    @PostMapping("/create")
    public String createChallengeWithBadge(
            @ModelAttribute("challenge") Challenge challenge,
            @RequestParam("badgeName") String badgeName,
            @RequestParam("badgeDescription") String badgeDescription,
            @RequestParam("badgeIconFile") MultipartFile badgeIconFile,
            @RequestParam("lockedIconFile") MultipartFile lockedIconFile,
            RedirectAttributes redirectAttributes, Principal principal) {

        try {
            User user = userService.getCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }

            String username = user.getUsername();

            // Use helper for file upload
            Badge badge = badgeService.handleBadgeIconUpload(badgeName,badgeDescription,badgeIconFile,lockedIconFile,username);
            // Save both challenge and badge
            challengeService.createChallengeWithBadge(challenge, badge);
            redirectAttributes.addFlashAttribute("success", "Challenge and badge created!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
            return "redirect:/challenge/create";
        }
        return "redirect:/challenge";
    }
}

