package com.healthyForum.service.challenge;

import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    private final UserChallengeRepository userChallengeRepository;
    private final EvidencePostRepository evidencePostRepository;

    public EvidenceService(UserChallengeRepository userChallengeRepository, EvidencePostRepository evidencePostRepository) {
        this.userChallengeRepository = userChallengeRepository;
        this.evidencePostRepository = evidencePostRepository;
    }

    public String saveEvidenceImage(MultipartFile file, String username) throws IOException {
        String evidenceDir = new File("src/main/resources/static/uploads/evidences/").getAbsolutePath();
        new File(evidenceDir).mkdirs();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String timestamp = java.time.LocalDateTime.now().format(formatter);
        String evidenceFileName = username + "_" + timestamp + "_" + file.getOriginalFilename();

        File evidenceFile = new File(evidenceDir + "/" + evidenceFileName);
        file.transferTo(evidenceFile);
        return "/uploads/evidences/" + evidenceFileName;
    }

    public List<EvidencePost> getAllEvidenceForSameChallenge(Integer userChallengeId, Long currentUserId) {
        UserChallenge baseUC = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int challengeId = baseUC.getChallenge().getId();

        List<UserChallenge> allUC = userChallengeRepository.findAllByChallengeId(challengeId);
        List<Integer> allUCIds = allUC.stream()
                .filter(uc -> !uc.getUser().getId().equals(currentUserId)) // exclude current user
                .map(UserChallenge::getId)
                .collect(Collectors.toList());

        return evidencePostRepository.findAllByUserChallengeIds(allUCIds);
    }

}
