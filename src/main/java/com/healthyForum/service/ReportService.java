package com.healthyForum.service;

import com.healthyForum.model.Report;
import com.healthyForum.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    // In src/main/java/com/healthyForum/service/ReportService.java

    public void respondToReport(Long id, String response) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            report.setResponse(response);
            reportRepository.save(report);
        }
    }
}