package com.healthyForum.service;

import com.healthyForum.model.Post;
import com.healthyForum.model.Report;
import com.healthyForum.model.User;
import com.healthyForum.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Transactional
    public Report createPostReport(User reporter, Post reportedPost, String reason) {
        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedPost(reportedPost);
        report.setReason(reason);
        report.setStatus(Report.ReportStatus.PENDING);
        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(Report.ReportStatus.PENDING);
    }
    @Transactional(readOnly = true)
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Transactional
    public Report resolveReport(Long reportId, String resolution) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo với ID: " + reportId));
        report.setStatus(Report.ReportStatus.RESOLVED);
        report.setResolution(resolution);
        report.setUpdatedAt(new Date());
        return reportRepository.save(report);
    }

    @Transactional
    public Report rejectReport(Long reportId, String resolution) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo với ID: " + reportId));
        report.setStatus(Report.ReportStatus.REJECTED);
        report.setResolution(resolution);
        report.setUpdatedAt(new Date());
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo với ID: " + reportId));
    }
    public List<Report> getReportsByReporter(User reporter) {
        return reportRepository.findByReporterOrderByCreatedAtDesc(reporter);
    }
}