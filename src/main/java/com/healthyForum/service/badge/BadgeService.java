package com.healthyForum.service.badge;

import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.BadgeRequirement;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.model.badge.UserBadgeId;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.badge.BadgeRepository;
import com.healthyForum.repository.badge.BadgeRequirementRepository;
import com.healthyForum.repository.badge.UserBadgeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final BadgeRequirementRepository badgeRequirementRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    public BadgeService(BadgeRepository badgeRepository, BadgeRequirementRepository badgeRequirementRepo, UserBadgeRepository userBadgeRepository, UserRepository userRepository) {
        this.badgeRepository = badgeRepository;
        this.badgeRequirementRepository = badgeRequirementRepo;
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> findById(int id){
        return badgeRepository.findById(id);
    }

    public void checkAndAwardBadge(Long userId, int challengeId) {
        List<BadgeRequirement> reqs = badgeRequirementRepository.findBySourceTypeNameAndSourceId("CHALLENGE", challengeId);

        for (BadgeRequirement req : reqs) {
            boolean hasBadge = userBadgeRepository.existsByIdUserIdAndIdBadgeId(userId, req.getBadge().getId());
            if (!hasBadge) {
                UserBadge ub = new UserBadge();
                ub.setId(new UserBadgeId(userId, req.getBadge().getId()));
                ub.setUser(userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
                ub.setBadge(req.getBadge());
                ub.setEarnedAt(LocalDateTime.now());
                ub.setDisplayed(false);
                userBadgeRepository.save(ub);
            }
        }
    }

    public Badge handleBadgeIconUpload(String badgeName, String badgeDescription, MultipartFile badgeIconFile, MultipartFile lockedIconFile, String username) throws IOException {
        Badge badge = new Badge();
        badge.setName(badgeName);
        badge.setDescription(badgeDescription);

        // 🎖 File Upload Setup
        Path projectRoot = Paths.get("").toAbsolutePath(); // current working dir
        Path unlockedDir = projectRoot.resolve("uploads").resolve("badges").resolve("unlocked");
        Path lockedDir = projectRoot.resolve("uploads").resolve("badges").resolve("locked");

        // Ensure directories exist
        Files.createDirectories(unlockedDir);
        Files.createDirectories(lockedDir);

        String iconFileName;
        String lockedFileName;

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String timestamp = java.time.LocalDateTime.now().format(formatter);
        iconFileName = username + "_" + timestamp + "_" + badgeIconFile.getOriginalFilename();
        lockedFileName = username + "_" + timestamp + "_" + lockedIconFile.getOriginalFilename();

        Path UnlockTargetFile = unlockedDir.resolve(iconFileName);
        badgeIconFile.transferTo(UnlockTargetFile.toFile());

        Path locTargetFile = lockedDir.resolve(lockedFileName);
        lockedIconFile.transferTo(locTargetFile.toFile());

        badge.setIcon("/uploads/badges/unlocked/" + iconFileName);
        badge.setLockedIcon("/uploads/badges/locked/" + lockedFileName);

        return badge;
    }
}
