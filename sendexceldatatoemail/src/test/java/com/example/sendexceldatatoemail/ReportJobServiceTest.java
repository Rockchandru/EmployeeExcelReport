package com.example.sendexceldatatoemail;

import com.example.reportjobrepository.ReportJobSummaryRepository;
import com.example.reportjobservice.ReportJobService;
import com.example.reportjobutil.ReportJobSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReportJobServiceTest {

    @Mock
    private ReportJobSummaryRepository repository;

    @InjectMocks
    private ReportJobService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMarkJobStarted_whenNoPreviousJob() {
        String jobName = "Report_summary_job_1";
        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.empty());

        service.markJobStarted(jobName);

        verify(repository).save(argThat(job ->
            job.getName().equals(jobName) &&
            job.getStatus().equals("Started") &&
            job.getTimestamp() != null
        ));
    }

    @Test
    void testMarkJobStarted_whenAlreadyStarted() {
        String jobName = "Report_summary_job_1";
        ReportJobSummary existing = new ReportJobSummary();
        existing.setName(jobName);
        existing.setStatus("Started");
        existing.setTimestamp(LocalDateTime.now());

        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.of(existing));

        service.markJobStarted(jobName);

        verify(repository, never()).save(any());
    }

    @Test
    void testMarkJobCompleted_whenJobExists() {
        String jobName = "Report_summary_job_1";
        ReportJobSummary existing = new ReportJobSummary();
        existing.setName(jobName);
        existing.setStatus("Started");
        existing.setTimestamp(LocalDateTime.now().minusSeconds(10));

        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.of(existing));

        service.markJobCompleted(jobName);

        verify(repository).save(argThat(job ->
            job.getName().equals(jobName) &&
            job.getStatus().equals("Completed") &&
            job.getDurationSeconds() >= 0
        ));
    }

    @Test
    void testMarkJobCompleted_whenJobMissing() {
        String jobName = "Report_summary_job_1";
        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.empty());

        service.markJobCompleted(jobName);

        verify(repository, never()).save(any());
    }

    @Test
    void testMarkJobFailed_whenJobExists() {
        String jobName = "Report_summary_job_1";
        ReportJobSummary existing = new ReportJobSummary();
        existing.setName(jobName);
        existing.setStatus("Started");
        existing.setTimestamp(LocalDateTime.now());

        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.of(existing));

        service.markJobFailed(jobName, "Simulated error");

        verify(repository).save(argThat(job ->
            job.getName().equals(jobName) &&
            job.getStatus().equals("Failed") &&
            job.getErrorMessage().equals("Simulated error")
        ));
    }

    @Test
    void testMarkJobFailed_whenJobMissing() {
        String jobName = "Report_summary_job_1";
        when(repository.findTopByNameOrderByTimestampDesc(jobName)).thenReturn(Optional.empty());

        service.markJobFailed(jobName, "Simulated error");

        verify(repository, never()).save(any());
    }
}

