package com.healthyForum.controller.admin.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.challenge.EvidencePost;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
@RequestMapping("/admin/evidence")
public class AdminEvidenceController {
    private final EvidencePostRepository evidencePostRepository;

    public AdminEvidenceController(EvidencePostRepository evidencePostRepository) {
        this.evidencePostRepository = evidencePostRepository;
    }

    @GetMapping("/review-evidence")
    public String viewUnderReviewEvidence(Model model) {
        List<EvidencePost> underReview = evidencePostRepository.findByStatus(EvidenceStatus.UNDER_REVIEW);
        model.addAttribute("underReview", underReview);
        return "admin/evidence/admin_review";
    }

    @PostMapping("/approve/{id}")
    public String manuallyApproveOrReject(@PathVariable Integer id, @RequestParam String action) {
        EvidencePost post = evidencePostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if ("APPROVE".equalsIgnoreCase(action)) {
            post.setStatus(EvidenceStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            post.setStatus(EvidenceStatus.REJECTED);
        }

        evidencePostRepository.save(post);
        return "redirect:/admin/evidence";
    }

}
