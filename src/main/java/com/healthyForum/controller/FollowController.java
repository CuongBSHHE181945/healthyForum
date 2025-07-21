package com.healthyForum.controller;

import com.healthyForum.model.User;
import com.healthyForum.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class FollowController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/follow")
    public String followUser(@PathVariable Long userId,
                             Principal principal,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser(principal);
        User targetUser = userService.getUserById(userId);

        if (currentUser == null || targetUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/";
        }

        if (currentUser.getId().equals(targetUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can't follow yourself.");
        } else {
            userService.follow(currentUser, targetUser);
            redirectAttributes.addFlashAttribute("success", "You are now following " + targetUser.getUsername());
        }

        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{userId}/unfollow")
    public String unfollowUser(@PathVariable Long userId,
                               Principal principal,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser(principal);
        User targetUser = userService.getUserById(userId);

        if (currentUser == null || targetUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/";
        }

        userService.unfollow(currentUser, targetUser);
        redirectAttributes.addFlashAttribute("success", "You have unfollowed " + targetUser.getUsername());

        return "redirect:" + request.getHeader("Referer");
    }
}
