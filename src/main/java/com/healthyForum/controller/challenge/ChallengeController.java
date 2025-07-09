package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.challenge.ChallengeService;
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
import java.util.Set;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserRepository userRepository;

    public ChallengeController(ChallengeService challengeService, UserRepository userRepository) {
        this.challengeService = challengeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showChallengeList(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<UserChallenge> active = challengeService.getActiveChallengesForUser(user);
        model.addAttribute("myChallenges", active);
        return "challenge/my-challenge";
    }

    @GetMapping("/{id}")
    public String showChallengeDetail(@PathVariable int id, Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Challenge challenge = challengeService.getChallengeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("challenge", challenge);
        List<Integer> joinedChallengeIds = challengeService.getJoinedChallengeIds(user);
        model.addAttribute("joinedIds", joinedChallengeIds);
        return "challenge/detail";
    }

    @PostMapping("/join/{id}")
    public String joinChallenge(@PathVariable int id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        challengeService.joinChallenge(user, id);
        boolean alreadyEarned = challengeService.hasUserEarnedBadge(user.getUserID(), id);
        if (alreadyEarned) {
            redirectAttributes.addFlashAttribute("warning", "You’ve already earned the badge for this challenge, but feel free to rejoin!");
        } else {
            redirectAttributes.addFlashAttribute("success", "Challenge joined successfully!");
        }
        return "redirect:/challenge";
    }
}

