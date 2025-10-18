package com.example.reportjobscheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.reportjobservice.ReportJobService;

@Component
public class ReportJobScheduler {

    @Autowired
    private ReportJobService jobService;

    @Scheduled(cron = "${send.report.expression}")
    public void runReportJob() {
        String location = "MVL";
        String jobName = "Report_summary_" + location;

        jobService.markJobStarted(jobName);

        try {
            // Generate report for MVL
            // Send email to MVL recipients
            jobService.markJobCompleted(jobName);
        } catch (Exception e) {
            jobService.markJobFailed(jobName, e.getMessage());
        }
    }
}