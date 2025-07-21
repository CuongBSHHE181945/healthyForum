package com.healthyForum.controller;

import com.healthyForum.model.Post.Post;
import com.healthyForum.model.Report;
import com.healthyForum.model.keywordFiltering.Keyword;
import com.healthyForum.repository.keywordFiltering.KeywordRepository;
import com.healthyForum.service.post.PostService;
import com.healthyForum.service.ReportService;
import com.healthyForum.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ManageController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private PostService postService;

    @Autowired
    private KeywordRepository keywordRepository;


    @GetMapping
    public String adminHome(Model model) {
        // Get counts for statistics
        long userCount = userService.getAllUsers().size();
        long postCount = postService.getAllPosts().size();
        long pendingReportCount = reportService.getReportsByStatus(Report.ReportStatus.PENDING).size();


        List<Report> recentReports = reportService.getAllReports();


        List<com.healthyForum.model.User> recentUsers = userService.getAllUsers();

        // Add data to model
        model.addAttribute("userCount", userCount);
        model.addAttribute("postCount", postCount);
        model.addAttribute("pendingReportCount", pendingReportCount);
        model.addAttribute("recentReports", recentReports);
        model.addAttribute("recentUsers", recentUsers);

        return "admin/admin";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/admin_users";
    }


    // Change these mappings from @GetMapping to @PostMapping

    @PostMapping("/suspend/{userId}")
    public String suspendUser(@PathVariable Long userId) {
        try {
            userService.suspendUser(userId);
            return "redirect:/admin/users";
        } catch (Exception e) {
            return "redirect:/admin/users?error=Failed to suspend user";
        }
    }

    @PostMapping("/unsuspend/{userId}")
    public String unsuspendUser(@PathVariable Long userId) {
        try {
            userService.unsuspendUser(userId);
            return "redirect:/admin/users";
        } catch (Exception e) {
            return "redirect:/admin/users?error=Failed to unsuspend user";
        }
    }


    // In src/main/java/com/healthyForum/controller/ManageController.java


    @GetMapping("/reports")
    public String manageReports(Model model) {
        // Get reports by status
        List<Report> pendingReports = reportService.getReportsByStatus(Report.ReportStatus.PENDING);
        List<Report> resolvedReports = reportService.getReportsByStatus(Report.ReportStatus.RESOLVED);
        List<Report> rejectedReports = reportService.getReportsByStatus(Report.ReportStatus.REJECTED);

        model.addAttribute("pendingReports", pendingReports);
        model.addAttribute("resolvedReports", resolvedReports);
        model.addAttribute("rejectedReports", rejectedReports);

        return "admin/admin_reports";
    }

    @GetMapping("/reports/all")
    public String viewAllReports(Model model) {
        model.addAttribute("allReports", reportService.getAllReports());
        return "admin/admin_all_reports";
    }

    @PostMapping("/reports/{id}/resolve")
    public String resolveReport(@PathVariable Long id,
                                @RequestParam String resolution,
                                @RequestParam(value = "banPost", required = false) Boolean banPost,
                                RedirectAttributes redirectAttributes) {

        Report report = reportService.getReportById(id);

        // Handle post banning if requested
        if (banPost != null && banPost && report.getReportedPost() != null) {
            Post bannedPost = postService.banPost(report.getReportedPost().getId());
            resolution += " (Bài viết đã bị cấm)";
        }

        // Resolve the report
        reportService.resolveReport(id, resolution);

        redirectAttributes.addFlashAttribute("success", "Báo cáo đã được xử lý thành công");
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{id}/reject")
    public String rejectReport(@PathVariable Long id,
                               @RequestParam String resolution,
                               RedirectAttributes redirectAttributes) {

        reportService.rejectReport(id, resolution);

        redirectAttributes.addFlashAttribute("success", "Báo cáo đã được từ chối");
        return "redirect:/admin/reports";
    }

    @GetMapping("/reports/{id}/ban-post")
    public String banPostDirectly(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Report report = reportService.getReportById(id);

        if (report.getReportedPost() != null) {
            // Ban the post
            postService.banPost(report.getReportedPost().getId());

            // Also resolve the report with auto-generated resolution
            String resolution = "Bài viết đã bị cấm do vi phạm quy định cộng đồng.";
            reportService.resolveReport(id, resolution);

            redirectAttributes.addFlashAttribute("success", "Bài viết đã bị cấm và báo cáo đã được xử lý");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể thực hiện - báo cáo này không liên quan đến bài viết");
        }

        return "redirect:/admin/reports";
    }

    @GetMapping("/posts/{id}/unban")
    public String unbanPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.unbanPost(id);
        redirectAttributes.addFlashAttribute("success", "Bài viết đã được bỏ cấm thành công");
        return "redirect:/admin/posts";
    }

    // In src/main/java/com/healthyForum/controller/ManageController.java

    @GetMapping("/keywords")
    public String listKeywords(Model model) {
        model.addAttribute("keywords", keywordRepository.findAll());
        return "keywords/list";
    }

    @PostMapping("/keywords/add")
    public String addKeyword(@RequestParam String word) {
        Keyword keyword = new Keyword();
        keyword.setWord(word);
        keywordRepository.save(keyword);
        return "redirect:/admin/keywords";
    }

    @PostMapping("/keywords/delete/{id}")
    public String deleteKeyword(@PathVariable Long id) {
        keywordRepository.deleteById(id);
        return "redirect:/admin/keywords";
    }

}
