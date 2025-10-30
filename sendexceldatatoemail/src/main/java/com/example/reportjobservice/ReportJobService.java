package com.example.reportjobservice;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reportjobrepository.ReportJobSummaryRepository;
import com.example.reportjobutil.ReportJobSummary;

@Service
public class ReportJobService {

    private static final Logger logger = LoggerFactory.getLogger(ReportJobService.class);

    
    @Autowired
    private ReportJobSummaryRepository repository;

 
    private Map<String, LocalDateTime> jobStartTimes = new HashMap<>();

    public void markJobStarted(String jobName) {
        Optional<ReportJobSummary> latest = repository.findTopByNameOrderByTimestampDesc(jobName);

        if (latest.isPresent() && "Started".equalsIgnoreCase(latest.get().getStatus())) {
            logger.info("Job '{}' is already running. Skipping start.", jobName);
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();
        jobStartTimes.put(jobName, startTime);

        ReportJobSummary job = new ReportJobSummary();
        job.setName(jobName);
        job.setTimestamp(startTime);
        job.setStatus("Started");
        repository.save(job);

        logger.info("Job '{}' marked as Started at {}.", jobName, startTime);
    }

    public void markJobCompleted(String jobName) {
        Optional<ReportJobSummary> latest = repository.findTopByNameOrderByTimestampDesc(jobName);
        LocalDateTime endTime = LocalDateTime.now();

        latest.ifPresent(job -> {
            LocalDateTime startTime = jobStartTimes.getOrDefault(jobName, job.getTimestamp());
            int duration = (int) Duration.between(startTime, endTime).getSeconds();

            job.setStatus("Completed");
            job.setDurationSeconds(duration);
            repository.save(job);

            logger.info("Job '{}' marked as Completed. Duration: {} seconds.", jobName, duration);
        });
    }

    public void markJobFailed(String jobName, String errorMessage) {
        Optional<ReportJobSummary> latest = repository.findTopByNameOrderByTimestampDesc(jobName);
        latest.ifPresent(job -> {
            job.setStatus("Failed");
            job.setErrorMessage(errorMessage);
            repository.save(job);

            logger.error("Job '{}' failed with error: {}", jobName, errorMessage);
        });
    }
}