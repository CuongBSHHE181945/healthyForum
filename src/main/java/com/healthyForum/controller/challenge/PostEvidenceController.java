package com.healthyForum.controller.challenge;

import com.healthyForum.model.*;
import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.challenge.*;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.ChallengeRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.challenge.EvidenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/evidence")
public class PostEvidenceController {

    private final PostRepository postRepository;
    private final EvidencePostRepository evidencePostRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EvidenceService evidenceService;

    public PostEvidenceController(PostRepository postRepository, EvidencePostRepository evidencePostRepository, ChallengeRepository challengeRepository, UserChallengeRepository userChallengeRepository, UserRepository userRepository, UserService userService, EvidenceService evidenceService) {
        this.postRepository = postRepository;
        this.evidencePostRepository = evidencePostRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.evidenceService = evidenceService;
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam Integer userChallengeId, Model model, Principal principal) {

        User user = userService.getCurrentUser(principal);
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new IllegalStateException("User has not joined this challenge."));

        model.addAttribute("post", new Post());
        model.addAttribute("userChallenge", userChallenge);
        model.addAttribute("challengeName", userChallenge.getChallenge().getName());

        return "evidence/create"; // Thymeleaf template
    }

    @PostMapping("/create")
    public String submitPostEvidence(@ModelAttribute("post") Post post,
                                     @RequestParam(required = false) MultipartFile imageFile,
                                     @RequestParam(required = false) Integer userChallengeId,
                                     @RequestParam(required = false, defaultValue = "false") boolean markAsEvidence,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) throws IOException {
        User user = userService.getCurrentUser(principal);
        // Fill post
        post.setUser(user);
        post.setDraft(false);
        post.setVisibility(Visibility.PUBLIC);
        post.setBanned(false);

        // Save image
        if (imageFile != null && !imageFile.isEmpty()) {
            String url = evidenceService.saveEvidenceImage(imageFile, user.getUsername());
            post.setImageUrl(url);
        }

        postRepository.save(post);

        if (markAsEvidence) {
            UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                    .orElseThrow(() -> new IllegalStateException("User has not joined this challenge."));

            EvidencePost evidence = new EvidencePost();
            evidence.setPost(post);
            evidence.setUserChallenge(userChallenge);
            evidence.setStatus(EvidenceStatus.PENDING);
            evidence.setVoteBased(false);
            evidence.setVoteTimeout(LocalDateTime.now().plusHours(24));
            evidence.setFallbackToAdmin(false);

            evidencePostRepository.save(evidence);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Evidence submitted!");
        return "redirect:/challenge/progress/" + userChallengeId;
    }


}

