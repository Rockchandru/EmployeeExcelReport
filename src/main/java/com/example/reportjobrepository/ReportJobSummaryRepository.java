package com.example.reportjobrepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reportjobutil.ReportJobSummary;

public interface ReportJobSummaryRepository extends JpaRepository<ReportJobSummary, Integer> {
    Optional<ReportJobSummary> findTopByNameOrderByTimestampDesc(String name);
}
