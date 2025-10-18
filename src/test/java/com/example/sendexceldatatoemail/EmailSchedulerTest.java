package com.example.sendexceldatatoemail;

import com.example.emailscheduler.EmailScheduler;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailSchedulerTest {

    @Mock
    private Emailservices emailService;

    @Mock
    private EmployeeSwipeRepository repository;

    @Mock
    private PdfReportGenerator pdfReportGenerator;

    @InjectMocks
    private EmailScheduler scheduler;

    private final LocalDateTime start = LocalDateTime.of(2025, 10, 12, 0, 0);
    private final LocalDateTime end = start.plusDays(1);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        scheduler.setRecipient1("test1@example.com");
        scheduler.setRecipient2("test2@example.com");
        scheduler.setRecipient3("test3@example.com");
        scheduler.setTestMode(true);
    }

    @Test
    void testSendPdfMail_success() throws Exception {
        Object[] row = new Object[] {
            0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L
        };
        //List<Object[]> rawResults = (List<Object[]>) (List<?>) List.of(row);
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(row);



        byte[] mockPdf = "PDF".getBytes();

        when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), eq("Pallavaram"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), eq(start.toLocalDate()))).thenReturn(mockPdf);

        assertDoesNotThrow(() -> scheduler.sendPdfMail());

        verify(emailService).sendWithAttachment(
            any(), contains("RFID Punching Report"), contains("Please find attached"), eq(mockPdf)
        );
    }


    @Test
    void testSendPdfMail_noDataFound_shouldNotSendEmail() {
        when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), eq("MVL"))).thenReturn(List.of());

        assertDoesNotThrow(() -> scheduler.sendPdfMail());

        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
    }

    @Test
    void testSendPdfMail_invalidEmail_shouldThrowException() {
        scheduler.setRecipient1("invalidemail"); // no @ or dot
        scheduler.setRecipient2("test2@example.com");
        scheduler.setRecipient3("test3@example.com");

        Object[] row = new Object[] {
            0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L
        };
        //List<Object[]> rawResults = (List<Object[]>) (List<?>) List.of(row);
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(row);


        when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), eq("MVL"))).thenReturn(rawResults);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scheduler.sendPdfMail();
        });

        assertTrue(exception.getMessage().contains("invalidemail"));
    }
}
