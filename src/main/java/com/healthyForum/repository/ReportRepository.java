package com.healthyForum.repository;

import com.healthyForum.model.Report;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(Report.ReportStatus status);
    List<Report> findByReporterOrderByCreatedAtDesc(User reporter);
}