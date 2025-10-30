package com.example.emailschedulertest;

import com.example.emailscheduler.EmailScheduler;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailSchedulerTest {

    @InjectMocks
    private EmailScheduler scheduler;

    @Mock private Emailservices emailService;
    @Mock private EmployeeSwipeRepository repository;
    @Mock private PdfReportGenerator pdfReportGenerator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        scheduler.setTestMode(true);
        scheduler.setCronEnabled(true);
        scheduler.addRecipient("valid@example.com");
        scheduler.addCc("cc@example.com");
    }

    @Test
    @DisplayName("✅ Add and remove TO recipient")
    void testAddAndRemoveRecipient() {
        scheduler.addRecipient("user@example.com");
        assertTrue(scheduler.getRecipients().contains("user@example.com"));

        scheduler.removeRecipient("user@example.com");
        assertFalse(scheduler.getRecipients().contains("user@example.com"));
    }

    @Test
    @DisplayName("✅ Add and remove CC recipient")
    void testAddAndRemoveCcRecipient() {
        scheduler.addCc("ccuser@example.com");
        assertTrue(scheduler.getCcRecipients().contains("ccuser@example.com"));

        scheduler.removeCc("ccuser@example.com");
        assertFalse(scheduler.getCcRecipients().contains("ccuser@example.com"));
    }

    @Test
    @DisplayName("✅ sendPdfMail - valid data and recipients")
    void testSendPdfMail_validData_shouldSend() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});

        scheduler.addRecipient("to@example.com");
        scheduler.addCc("cc@example.com");

        scheduler.sendPdfMail();

        verify(emailService).sendWithAttachment(
            any(String[].class),
            any(String[].class),
            anyString(),
            anyString(),
            any(byte[].class),
            anyString()
        );
    }

    @Test
    @DisplayName("⚠️ sendPdfMail - no TO recipients")
    void testSendPdfMail_noRecipients_shouldSkip() {
        scheduler.getRecipients().clear();
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("❌ sendPdfMail - invalid TO email should skip")
    void testSendPdfMail_invalidEmail_shouldSkip() {
        scheduler.getRecipients().clear();
        scheduler.addRecipient("invalid-email");

        scheduler.sendPdfMail(); // should not throw
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("⚠️ sendPdfMail - cron disabled")
    void testSendPdfMail_cronDisabled_shouldSkip() {
        scheduler.setCronEnabled(false);
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("⚠️ sendPdfMail - empty data")
    void testSendPdfMail_emptyData_shouldSkip() {
        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(Collections.emptyList());
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any(), any(), any());
    }
}













/*package com.example.emailschedulertest;

import com.example.emailscheduler.EmailScheduler;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailSchedulerTest {

    @InjectMocks
    private EmailScheduler scheduler;

    @Mock private Emailservices emailService;
    @Mock private EmployeeSwipeRepository repository;
    @Mock private PdfReportGenerator pdfReportGenerator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        scheduler.setTestMode(true);
        scheduler.setCronEnabled(true);
        scheduler.addRecipient("valid@example.com");
    }

    @Test
    @DisplayName("✅ Add and remove recipient")
    void testAddAndRemoveRecipient() {
        scheduler.addRecipient("user@example.com");
        assertTrue(scheduler.getRecipients().contains("user@example.com"));

        scheduler.removeRecipient("user@example.com");
        assertFalse(scheduler.getRecipients().contains("user@example.com"));
    }

    @Test
    @DisplayName("✅ sendPdfMail - valid data and recipient")
    void testSendPdfMail_validData_shouldSend() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});

        scheduler.sendPdfMail();
        verify(emailService).sendWithAttachment(any(), any(), any(), any());
    }

    @Test
    @DisplayName("⚠️ sendPdfMail - no recipients")
    void testSendPdfMail_noRecipients_shouldSkip() {
        scheduler.getRecipients().clear();
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
    }

    @Test
    @DisplayName("❌ sendPdfMail - invalid email should skip")
    void testSendPdfMail_invalidEmail_shouldSkip() {
        scheduler.getRecipients().clear();
        scheduler.addRecipient("invalid-email");

        scheduler.sendPdfMail(); // should not throw
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
    }


    @Test
    @DisplayName("⚠️ sendPdfMail - cron disabled")
    void testSendPdfMail_cronDisabled_shouldSkip() {
        scheduler.setCronEnabled(false);
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
    }

    @Test
    @DisplayName("⚠️ sendPdfMail - empty data")
    void testSendPdfMail_emptyData_shouldSkip() {
        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(Collections.emptyList());
        scheduler.sendPdfMail();
        verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
    }
}


























/*package com.example.sendexceldatatoemail;

import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EmailSchedulerTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailSchedulerTest.class);

    @Autowired private Emailservices emailService;
    @Autowired private EmployeeSwipeRepository repository;
    @Autowired private PdfReportGenerator pdfReportGenerator;

    private final List<String> recipients = new CopyOnWriteArrayList<>();
    private boolean testMode = false;

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void addRecipient(String email) {
        recipients.add(email);
    }

    public void removeRecipient(String email) {
        recipients.remove(email);
    }

    @Scheduled(cron = "${send.email.expression}")
    public void sendPdfMail() throws Exception {
        logger.info("📅 Scheduled task triggered: Preparing to send tower-wise PDF report email.");

        LocalDateTime start = LocalDateTime.of(2025, 10, 13, 0, 0); // Replace with dynamic if needed
        LocalDateTime end = start.plusDays(1);
        String location = "MVL";

        logger.debug("Fetching swipe summary for location={} between {} and {}", location, start, end);
        List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end, location);

        Set<String> seenKeys = new HashSet<>();
        List<EmployeeFloorSummary> summaries = rawResults.stream()
            .map(row -> new EmployeeFloorSummary(
                (Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                ((Number) row[8]).longValue()))
            .filter(s -> seenKeys.add(s.getEmployeeId() + "-" + s.getDesignation()))
            .toList();

        if (summaries.isEmpty()) {
            logger.warn("⚠️ No swipe data found for {} between {} and {}", location, start, end);
            return;
        }

        logger.info("📄 Generating PDF report for {} records.", summaries.size());
        byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());

        for (String email : recipients) {
            if (email == null || !email.matches(".+@.+\\..+")) {
                logger.error("❌ Invalid email address: {}", email);
                if (testMode) throw new IllegalArgumentException("Invalid email: " + email);
                return;
            }
        }

        String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        String subject = "RFID Punching Report – " + location + " – " + formattedDate;
        String body = "Dear Team,<br><br>" +
            "Please find attached the RFID Punching Report for <strong>" + location + "</strong> on <strong>" + formattedDate + "</strong>.<br><br>" +
            "Thank you,<br><span style='font-size:13px;'>Chandru</span>";

        logger.info("📧 Sending email to recipients: {}", String.join(", ", recipients));
        emailService.sendWithAttachment(recipients.toArray(new String[0]), subject, body, report);

        logger.info("✅ Email sent successfully for {}", formattedDate);
    }
}
*/





























/*
 * 
 
 * package com.example.sendexceldatatoemail;
 * 
 * import com.example.emailscheduler.EmailScheduler; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.*;
 * 
 * import java.time.LocalDateTime; import java.util.List;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.Mockito.*;
 * 
 * public class EmailSchedulerTest {
 * 
 * @Mock private Emailservices emailService;
 * 
 * @Mock private EmployeeSwipeRepository repository;
 * 
 * @Mock private PdfReportGenerator pdfReportGenerator;
 * 
 * @InjectMocks private EmailScheduler scheduler;
 * 
 * private final LocalDateTime start = LocalDateTime.of(2025, 10, 17, 0, 0);
 * private final LocalDateTime end = start.plusDays(1);
 * 
 * @BeforeEach void setup() { MockitoAnnotations.openMocks(this);
 * scheduler.setTestMode(true); scheduler.getRecipients().clear();
 * scheduler.getRecipients().add("test1@example.com");
 * scheduler.getRecipients().add("test2@example.com"); }
 * 
 * @Test void testSendPdfMail_success() throws Exception { Object[] row = new
 * Object[] { 0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L };
 * List<Object[]> rawResults = List.of(row); // ✅ Correct type: List<Object[]>
 * byte[] mockPdf = "PDF".getBytes();
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * when(pdfReportGenerator.generateTowerSummaryPdf(anyList(),
 * eq(start.toLocalDate()))).thenReturn(mockPdf);
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService).sendWithAttachment( any(),
 * contains("RFID Punching Report"), contains("John Doe"), eq(mockPdf) ); }
 * 
 * @Test void testSendPdfMail_noDataFound_shouldNotSendEmail() {
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(List.of());
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
 * }
 * 
 * @Test void testSendPdfMail_invalidEmail_shouldThrowException() {
 * scheduler.getRecipients().clear();
 * scheduler.getRecipients().add("invalidemail"); // ❌ Invalid format
 * 
 * Object[] row = new Object[] { 0, "E001", "John Doe", "Security", 10L, 12L,
 * 8L, 9L, 11L }; List<Object[]> rawResults = List.of(row); // ✅ Correct
 * wrapping
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * 
 * IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> { scheduler.sendPdfMail();
 * });
 * 
 * assertTrue(exception.getMessage().contains("invalidemail")); } }
 */
/*
 * package com.example.sendexceldatatoemail;
 * 
 * import com.example.emailscheduler.EmailScheduler; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.*;
 * 
 * import java.time.LocalDateTime; import java.util.List;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.Mockito.*;
 * 
 * public class EmailSchedulerTest {
 * 
 * @Mock private Emailservices emailService;
 * 
 * @Mock private EmployeeSwipeRepository repository;
 * 
 * @Mock private PdfReportGenerator pdfReportGenerator;
 * 
 * @InjectMocks private EmailScheduler scheduler;
 * 
 * private final LocalDateTime start = LocalDateTime.of(2025, 10, 17, 0, 0);
 * private final LocalDateTime end = start.plusDays(1);
 * 
 * @BeforeEach void setup() { MockitoAnnotations.openMocks(this);
 * scheduler.setTestMode(true);
 * scheduler.getRecipients().add("test1@example.com");
 * scheduler.getRecipients().add("test2@example.com"); }
 * 
 * @Test void testSendPdfMail_success() throws Exception { Object[] row = new
 * Object[] { 0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L };
 * List<Object[]> rawResults = List.of(row); byte[] mockPdf = "PDF".getBytes();
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * when(pdfReportGenerator.generateTowerSummaryPdf(anyList(),
 * eq(start.toLocalDate()))).thenReturn(mockPdf);
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService).sendWithAttachment( any(),
 * contains("RFID Punching Report"), contains("John Doe"), eq(mockPdf) ); }
 * 
 * @Test void testSendPdfMail_noDataFound_shouldNotSendEmail() {
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(List.of());
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
 * }
 * 
 * @Test void testSendPdfMail_invalidEmail_shouldThrowException() {
 * scheduler.getRecipients().clear();
 * scheduler.getRecipients().add("invalidemail");
 * 
 * Object[] row = new Object[] { 0, "E001", "John Doe", "Security", 10L, 12L,
 * 8L, 9L, 11L }; List<Object[]> rawResults = List.of(row);
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * 
 * IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> { scheduler.sendPdfMail();
 * });
 * 
 * assertTrue(exception.getMessage().contains("invalidemail")); } }
 */

/*
 * package com.example.sendexceldatatoemail;
 * 
 * import com.example.emailscheduler.EmailScheduler; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.*;
 * 
 * import java.time.LocalDateTime; import java.util.ArrayList; import
 * java.util.List;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.Mockito.*;
 * 
 * public class EmailSchedulerTest {
 * 
 * @Mock private Emailservices emailService;
 * 
 * @Mock private EmployeeSwipeRepository repository;
 * 
 * @Mock private PdfReportGenerator pdfReportGenerator;
 * 
 * @InjectMocks private EmailScheduler scheduler;
 * 
 * private final LocalDateTime start = LocalDateTime.of(2025, 10, 17, 0, 0);
 * private final LocalDateTime end = start.plusDays(1);
 * 
 * @BeforeEach void setup() { MockitoAnnotations.openMocks(this);
 * scheduler.setRecipient1("test1@example.com");
 * scheduler.setRecipient2("test2@example.com");
 * scheduler.setRecipient3("test3@example.com"); scheduler.setTestMode(true); }
 * 
 * @Test void testSendPdfMail_success() throws Exception { Object[] row = new
 * Object[] { 0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L };
 * //List<Object[]> rawResults = (List<Object[]>) (List<?>) List.of(row);
 * List<Object[]> rawResults = new ArrayList<>(); rawResults.add(row);
 * 
 * 
 * 
 * byte[] mockPdf = "PDF".getBytes();
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * when(pdfReportGenerator.generateTowerSummaryPdf(anyList(),
 * eq(start.toLocalDate()))).thenReturn(mockPdf);
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService).sendWithAttachment( any(),
 * contains("RFID Punching Report"), contains("Please find attached"),
 * eq(mockPdf) ); }
 * 
 * 
 * @Test void testSendPdfMail_noDataFound_shouldNotSendEmail() {
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(List.of());
 * 
 * assertDoesNotThrow(() -> scheduler.sendPdfMail());
 * 
 * verify(emailService, never()).sendWithAttachment(any(), any(), any(), any());
 * }
 * 
 * @Test void testSendPdfMail_invalidEmail_shouldThrowException() {
 * scheduler.setRecipient1("invalidemail"); // no @ or dot
 * scheduler.setRecipient2("test2@example.com");
 * scheduler.setRecipient3("test3@example.com");
 * 
 * Object[] row = new Object[] { 0, "E001", "John Doe", "Security", 10L, 12L,
 * 8L, 9L, 11L }; //List<Object[]> rawResults = (List<Object[]>) (List<?>)
 * List.of(row); List<Object[]> rawResults = new ArrayList<>();
 * rawResults.add(row);
 * 
 * 
 * when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * eq("MVL"))).thenReturn(rawResults);
 * 
 * IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> { scheduler.sendPdfMail();
 * });
 * 
 * assertTrue(exception.getMessage().contains("invalidemail")); } }
 */