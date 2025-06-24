package com.healthyForum.controller;

import com.healthyForum.service.ReportService;
import com.healthyForum.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class ManageController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ReportService reportService;


    @GetMapping
    public String adminHome() {
        return "admin/admin";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/admin_users";
    }

    @GetMapping("/reports")
    public String getReports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/admin_reports";
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


    @PostMapping("/reports/respond/{id}")
    public String respondReport(@PathVariable Long id, @RequestParam String response) {
        reportService.respondToReport(id, response);
        return "redirect:/admin/reports";
    }



}