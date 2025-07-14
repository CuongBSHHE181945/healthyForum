package com.healthyForum.service.challenge;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class EvidenceService {
    public String saveEvidenceImage(MultipartFile file, String username) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String evidenceDir = new File("src/main/resources/static/uploads/evidences/").getAbsolutePath();
        new File(evidenceDir).mkdirs();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String timestamp = java.time.LocalDateTime.now().format(formatter);
        String evidenceFileName = username + "_" + timestamp + "_" + file.getOriginalFilename();

        File evidenceFile = new File(evidenceDir + "/" + evidenceFileName);
        file.transferTo(evidenceFile);
        return "/uploads/evidences/" + filename;
    }
}
