package com.healthyForum.controller;


import com.healthyForum.model.SleepEntry;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.SleepRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.util.DateValidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/sleepTracker")
public class SleepController {
    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    public SleepController(SleepRepository sleepEntryRepository, UserRepository userRepository, UserAccountRepository userAccountRepository) {
        this.sleepRepository = sleepEntryRepository;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/list")
    public String showSleepList(
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            Pageable pageable,
            Principal principal
    ) {
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        Page<SleepEntry> sleepEntries;

        if(DateValidate.hasValidDateRange(startDate, endDate)){
            sleepEntries = sleepRepository.findByUserAndDateBetween(user, startDate, endDate, pageRequest);
        } else {
            if (startDate != null && endDate != null)
                model.addAttribute("errorMessage", "Invalid date range");
            sleepEntries = sleepRepository.findByUser(user, pageRequest);
        }

        model.addAttribute("sleepEntries", sleepEntries);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDir", dir);
        return "sleepTracker/sleepList"; // refers to templates/sleepList.html
    }

    //test mapping
    @GetMapping("/list-json")
    public ResponseEntity<List<SleepEntry>> getSleepEntriesJson(Pageable pageable, Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Page<SleepEntry> page = sleepRepository.findByUser(user,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "date"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/form")
    public String showSleepEntryForm(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        SleepEntry sleepEntry = new SleepEntry();
//        sleepEntry.setUser(user);
//
        model.addAttribute("sleepEntry", sleepEntry);
        return "sleepTracker/sleepForm";
    }

    @GetMapping("/edit/{sleepId}")
    public String editSleepEntry(@PathVariable Long sleepId, Model model) {
        SleepEntry sleepEntry = sleepRepository.findById(sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
        model.addAttribute("sleepEntry", sleepEntry);
        return "sleepTracker/sleepForm"; // refers to templates/editSleepEntry.html
    }

    @PostMapping
    public ResponseEntity<Void> createSleepEntry(@RequestBody SleepEntry newSleepEntryRequest,
                                                 UriComponentsBuilder ucb,
                                                 Principal principal) {
        // Find the logged-in user
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Create a new SleepEntry with the user attached
        SleepEntry sleepEntryToSave = new SleepEntry(
                null,
                newSleepEntryRequest.getDate(),
                newSleepEntryRequest.getStartTime(),
                newSleepEntryRequest.getEndTime(),
                newSleepEntryRequest.getQuality(),
                newSleepEntryRequest.getNotes(),
                user
        );

        SleepEntry saved = sleepRepository.save(sleepEntryToSave);

        // Build URI for newly created entry
        URI location = ucb.path("/sleepTracker/sleep/{id}").buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/form/submit")
    public String handleSleepForm(@ModelAttribute SleepEntry sleepEntry, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ❌ Reject future dates
        if (DateValidate.dateIsInFuture(sleepEntry.getDate())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sleep date cannot be in the future.");
            return "redirect:/sleepTracker/form";
        }

        // ✅ Duration validation
        if (DateValidate.timeCalMin(sleepEntry.getStartTime(),sleepEntry.getEndTime(), sleepEntry.getDate()) > 960) { // e.g., more than 16 hours
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Are you sure you slept that long?");
            return "redirect:/sleepTracker/form";
        }

        if (sleepEntry.getId() == null) {
            // Create new entry
            sleepEntry.setUser(user);
            sleepRepository.save(sleepEntry);
        } else {
            // Update existing entry — only update allowed fields
            SleepEntry existingEntry = sleepRepository.findById(sleepEntry.getId())
                    .orElseThrow(() -> new RuntimeException("Sleep entry not found"));

            // Only allow updating editable fields
            existingEntry.setQuality(sleepEntry.getQuality());
            existingEntry.setNotes(sleepEntry.getNotes());

            sleepRepository.save(existingEntry);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Entry saved successfully!");
        return "redirect:/sleepTracker/list";
    }

    /**
     * Helper method to get current user from Principal (handles both local and OAuth authentication)
     */
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        // If principal is OAuth2User, try to get email and find by email
        if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                return userRepository.findByEmail(email).orElse(null);
            }
            String googleId = oauth2User.getName();
            if (googleId != null) {
                UserAccount account = userAccountRepository.findByGoogleId(googleId).orElse(null);
                if (account != null) return account.getUser();
            }
        }
        String principalName = principal.getName();
        UserAccount account = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
        if (account != null) {
            return account.getUser();
        }
        User user = userRepository.findByEmail(principalName).orElse(null);
        if (user != null) {
            return user;
        }
        return null;
    }

//    public static void main(String[] args) {
//        Model model = new Model();
//        Pageable pageable = new Pageable();
//        System.out.println(showSleepList(model, pageable, "sarah1"));
//    }
}