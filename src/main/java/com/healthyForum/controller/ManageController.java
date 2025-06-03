package com.healthyForum.controller;

import com.healthyForum.service.BlogService;
import com.healthyForum.service.FeedbackService;
import com.healthyForum.service.ReportService;
import com.healthyForum.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class ManageController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String adminHome() {
        return "admin";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin_users";
    }

    @GetMapping("/reports")
    public String getReports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin_reports";
    }


    @GetMapping("/feedbacks")
    public String getFeedbacks(Model model) {
        try {
            model.addAttribute("feedbacks", feedbackService.getAllFeedback());
            return "admin_feedbacks";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading feedbacks: " + e.getMessage());
            return "admin_feedbacks";
        }
    }

    @GetMapping("/blogs")
    public String getBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "admin_blogs";
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


    @PostMapping("/feedbacks/respond/{id}")
    public String respondFeedback(@PathVariable Long id, @RequestParam String response) {
        feedbackService.respondToFeedback(id, response);
        return "redirect:/admin/feedbacks";
    }

    @PostMapping("/reports/respond/{id}")
    public String respondReport(@PathVariable Long id, @RequestParam String response) {
        reportService.respondToReport(id, response);
        return "redirect:/admin/reports";
    }

    @PostMapping("/blogs/suspend/{id}")
    public String suspendBlog(@PathVariable Long id) {
        blogService.suspendBlog(id);
        return "redirect:/admin/blogs";
    }

    @PostMapping("/blogs/unsuspend/{id}")
    public String unsuspendBlog(@PathVariable Long id) {
        blogService.unsuspendBlog(id);
        return "redirect:/admin/blogs";
    }


}