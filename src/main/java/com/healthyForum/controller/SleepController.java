package com.healthyForum.controller;


import com.healthyForum.model.SleepEntry;
import com.healthyForum.model.User;
import com.healthyForum.repository.SleepRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/sleep")
public class SleepController {
    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;

    public SleepController(SleepRepository sleepEntryRepository, UserRepository userRepository) {
        this.sleepRepository = sleepEntryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/list")
    public String showSleepList(
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            Model model,
            Pageable pageable,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        Page<SleepEntry> sleepEntries = sleepRepository.findByUser(user, pageRequest);

        model.addAttribute("sleepEntries", sleepEntries);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDir", dir);
        return "sleepList"; // refers to templates/sleepList.html
    }

    //test mapping
    @GetMapping("/list-json")
    public ResponseEntity<List<SleepEntry>> getSleepEntriesJson(Pageable pageable, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SleepEntry sleepEntry = new SleepEntry();
//        sleepEntry.setUser(user);
//
        model.addAttribute("sleepEntry", sleepEntry);
        return "sleepForm";
    }

    @PostMapping
    public ResponseEntity<Void> createSleepEntry(@RequestBody SleepEntry newSleepEntryRequest,
                                                 UriComponentsBuilder ucb,
                                                 Principal principal) {
        // Find the logged-in user
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
        URI location = ucb.path("/sleep/{id}").buildAndExpand(saved.getSleepId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/form/submit")
    public String handleSleepForm(@ModelAttribute SleepEntry sleepEntry, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        sleepEntry.setUser(user);
        sleepRepository.save(sleepEntry);

        // Add flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Entry saved successfully!");

        return "redirect:/sleep/list";
    }

//    public static void main(String[] args) {
//        Model model = new Model();
//        Pageable pageable = new Pageable();
//        System.out.println(showSleepList(model, pageable, "sarah1"));
//    }
}