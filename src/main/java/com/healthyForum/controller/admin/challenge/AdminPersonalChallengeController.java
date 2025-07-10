package com.healthyForum.controller.admin.challenge;


import com.healthyForum.model.User;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.model.challenge.ChallengeCategory;
import com.healthyForum.repository.challenge.ChallengeCategoryRepository;
import com.healthyForum.repository.challenge.ChallengeTypeRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.challenge.ChallengeService;
import lombok.Value;
import org.springframework.http.HttpStatus;
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
import java.util.UUID;

@Controller
@RequestMapping("/admin/personal-challenge")
public class AdminPersonalChallengeController {

        private final ChallengeService challengeService;
        private final ChallengeCategoryRepository challengeCategoryRepository;
        private final ChallengeTypeRepository challengeTypeRepository;
        private final UserService userService;
        private final BadgeService badgeService;

    public AdminPersonalChallengeController(ChallengeService challengeService, ChallengeCategoryRepository challengeCategoryRepository, ChallengeTypeRepository challengeTypeRepository, UserService userService, BadgeService badgeService) {
        this.challengeService = challengeService;
        this.challengeCategoryRepository = challengeCategoryRepository;
        this.challengeTypeRepository = challengeTypeRepository;
        this.userService = userService;
        this.badgeService = badgeService;
    }

    @GetMapping
    public String listPersonalChallenges(Model model) {
        model.addAttribute("challenges", challengeService.getAllPersonalChallenges());
        return "admin/personal_challenge/list";
    }

        @GetMapping("/create")
        public String showCreateForm(Model model) {
            Challenge challenge = new Challenge();
            List<ChallengeCategory> categories = challengeCategoryRepository.findAll();

            model.addAttribute("challenge", challenge);
            model.addAttribute("badge", new Badge());
            model.addAttribute("categories", categories);
            return "admin/personal_challenge/create";
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

            // Generate file names
            User user = userService.getCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }
            String username = user.getUsername();

            Badge badge = badgeService.handleBadgeIconUpload(badgeName,badgeDescription,badgeIconFile,lockedIconFile,username);

            // Save both challenge and badge (you can wrap this in a service)
            challengeService.createChallengeWithBadge(challenge, badge);

            redirectAttributes.addFlashAttribute("success", "Challenge and badge created!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
            return "redirect:/admin/personal-challenge/create";
        }

        return "redirect:/admin/personal-challenge";
    }


    @GetMapping("/edit/{id}")
        public String edit(@PathVariable int id, Model model) {
            Challenge challenge = challengeService.getChallengeById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            model.addAttribute("challenge", challenge);
            return "admin/personal_challenge/edit";
        }

        @PostMapping("/edit")
        public String update(@ModelAttribute("challenge") Challenge challenge, RedirectAttributes redirectAttributes) {
            challengeService.updateChallenge(challenge);
            redirectAttributes.addFlashAttribute("success", "Challenge updated successfully!");
            return "redirect:/admin/personal-challenge";
        }

        @GetMapping("/delete/{id}")
        public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
            challengeService.deleteChallenge(id);
            redirectAttributes.addFlashAttribute("success", "Challenge deleted successfully!");
            return "redirect:/admin/personal-challenge";
        }

        @GetMapping("/toggle-status/{id}")
        public String toggleStatus(@PathVariable int id) {
            Challenge challenge = challengeService.getChallengeById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (challenge != null) {
                challenge.setStatus(!challenge.isStatus());
                challengeService.updateChallenge(challenge);
            }
            return "redirect:/admin/personal-challenge";
        }
}
