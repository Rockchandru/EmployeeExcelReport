/*package com.example.sendexceldatatoemail;

import com.example.reportjobscheduler.ReportJobScheduler;
import com.example.reportjobservice.ReportJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

public class ReportJobSchedulerTest {

    @Mock
    private ReportJobService jobService;

    @InjectMocks
    private ReportJobScheduler scheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunReportJob_triggersLifecycle_success() {
        scheduler.runReportJob();

        verify(jobService).markJobStarted("Report_summary_MVL");
        verify(jobService).markJobCompleted("Report_summary_MVL");
        verify(jobService, never()).markJobFailed(anyString(), anyString());
    }

    @Test
    void testRunReportJob_triggersFailure_onException() {
        // Simulate failure by throwing exception when markJobCompleted is called
        doThrow(new RuntimeException("Simulated failure"))
            .when(jobService).markJobCompleted("Report_summary_MVL");

        scheduler.runReportJob();

        verify(jobService).markJobStarted("Report_summary_MVL");
        verify(jobService).markJobCompleted("Report_summary_MVL");
        verify(jobService).markJobFailed(eq("Report_summary_MVL"), contains("Simulated failure"));
    }
}*/
