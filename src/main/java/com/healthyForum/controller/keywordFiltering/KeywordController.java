package com.healthyForum.controller.keywordFiltering;


import com.healthyForum.model.keywordFiltering.BannedKeyword;
import com.healthyForum.service.keywordFiltering.ContentFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/keywords")
@PreAuthorize("hasRole('ADMIN')")
public class KeywordController {

    @Autowired
    private ContentFilterService contentFilterService;

    @GetMapping
    public String listKeywords(Model model) {
        model.addAttribute("keywords", contentFilterService.getAllKeywords());
        model.addAttribute("newKeyword", new BannedKeyword()); // Add this line
        return "admin/keywords";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("keyword", new BannedKeyword());
        return "admin/keywords";
    }

    @PostMapping("/add")
    public String addKeyword(@ModelAttribute BannedKeyword keyword, RedirectAttributes redirectAttributes) {
        contentFilterService.addKeyword(keyword);
        redirectAttributes.addFlashAttribute("success", "Keyword added successfully");
        return "redirect:/admin/keywords";
    }

    @PostMapping("/test-filter")
    @ResponseBody
    public String testFilter(@RequestParam String content) {
        return contentFilterService.filterContent(content);
    }
}
