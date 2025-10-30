package com.example.emailcontrollertest;

import com.example.backup.SiteBackupScheduler;
import com.example.dto.EmployeeFloorSummary;
import com.example.emailcontroller.Maincontroller;
import com.example.emailservice.Emailservices;
import com.example.emailscheduler.EmailScheduler;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MaincontrollerTest {

    @InjectMocks
    private Maincontroller controller;

    @Mock private Emailservices emailService;
    @Mock private EmployeeSwipeRepository repository;
    @Mock private PdfReportGenerator pdfReportGenerator;
    @Mock private EmailScheduler scheduler;
    @Mock private SiteBackupScheduler backupScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ /send-tower-summary - success")
    void testSendTowerSummaryEmail_success() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});
        when(scheduler.getRecipients()).thenReturn(List.of("to@example.com"));
        when(scheduler.getCcRecipients()).thenReturn(List.of("cc@example.com"));

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("✅"));

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
    @DisplayName("⚠️ /send-tower-summary - no data")
    void testSendTowerSummaryEmail_noData() {
        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(Collections.emptyList());

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("⚠️"));
    }

    @Test
    @DisplayName("❌ /send-tower-summary - invalid TO email")
    void testSendTowerSummaryEmail_invalidToEmail() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});
        when(scheduler.getRecipients()).thenReturn(List.of("invalid-email"));
        when(scheduler.getCcRecipients()).thenReturn(List.of("cc@example.com"));

        doThrow(new IllegalArgumentException("Invalid TO email")).when(emailService)
            .sendWithAttachment(any(), any(), anyString(), anyString(), any(byte[].class), anyString());

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("❌"));

        verify(emailService).sendWithAttachment(any(), any(), anyString(), anyString(), any(byte[].class), anyString());
    }

    // Remaining tests unchanged — all use correct List<Object[]> pattern

    @Test
    @DisplayName("✅ /cron/toggle")
    void testToggleCron() {
        ResponseEntity<String> response = controller.toggleCron(true);
        assertEquals("Cron job enabled", response.getBody());
        verify(scheduler).setCronEnabled(true);
    }

    @Test
    @DisplayName("✅ /recipients/add")
    void testAddRecipient() {
        ResponseEntity<String> response = controller.addRecipient("to@example.com");
        assertEquals("Added recipient: to@example.com", response.getBody());
        verify(scheduler).addRecipient("to@example.com");
    }

    @Test
    @DisplayName("✅ /recipients/remove")
    void testRemoveRecipient() {
        ResponseEntity<String> response = controller.removeRecipient("to@example.com");
        assertEquals("Removed recipient: to@example.com", response.getBody());
        verify(scheduler).removeRecipient("to@example.com");
    }

    @Test
    @DisplayName("✅ /recipients - list")
    void testListRecipients() {
        when(scheduler.getRecipients()).thenReturn(List.of("a@example.com", "b@example.com"));
        ResponseEntity<List<String>> response = controller.listRecipients();
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("a@example.com"));
    }

    @Test
    @DisplayName("✅ /recipients/clear")
    void testClearRecipients() {
        ResponseEntity<String> response = controller.clearAllRecipients();
        assertEquals("✅ All TO recipients have been removed.", response.getBody());
        verify(scheduler).clearRecipients();
    }

    @Test
    @DisplayName("✅ /cc/add")
    void testAddCcRecipient() {
        ResponseEntity<String> response = controller.addCc("cc@example.com");
        assertEquals("Added CC recipient: cc@example.com", response.getBody());
        verify(scheduler).addCc("cc@example.com");
    }

    @Test
    @DisplayName("✅ /cc/remove")
    void testRemoveCcRecipient() {
        ResponseEntity<String> response = controller.removeCc("cc@example.com");
        assertEquals("Removed CC recipient: cc@example.com", response.getBody());
        verify(scheduler).removeCc("cc@example.com");
    }

    @Test
    @DisplayName("✅ /cc - list")
    void testListCcRecipients() {
        when(scheduler.getCcRecipients()).thenReturn(List.of("cc1@example.com", "cc2@example.com"));
        ResponseEntity<List<String>> response = controller.listCcRecipients();
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("cc1@example.com"));
    }

    @Test
    @DisplayName("✅ /cc/clear")
    void testClearCcRecipients() {
        ResponseEntity<String> response = controller.clearAllCcRecipients();
        assertEquals("✅ All CC recipients have been removed.", response.getBody());
        verify(scheduler).clearCcRecipients();
    }

    @Test
    @DisplayName("✅ /summary - returns parsed DTO")
    void testGetSummary() {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);

        ResponseEntity<List<EmployeeFloorSummary>> response = controller.getSummary("2025-10-13");
        assertFalse(response.getBody().isEmpty());
        assertEquals("E001", response.getBody().get(0).getEmployeeId());
    }

    @Test
    @DisplayName("✅ /summary/csv - returns CSV bytes")
    void testGetCsvReport() {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);

        ResponseEntity<byte[]> response = controller.getCsvReport("2025-10-13");
        String csv = new String(response.getBody());
        assertTrue(csv.contains("Employee ID"));
        assertTrue(csv.contains("John"));
    }

    @Test
    @DisplayName("✅ /backup/trigger")
    void testTriggerBackup() {
        when(backupScheduler.triggerBackupManually()).thenReturn("✅ Backup completed");

        ResponseEntity<String> response = controller.triggerBackup();
        assertEquals("✅ Backup completed", response.getBody());
    }
}










/*package com.example.emailcontrollertest;

import com.example.backup.SiteBackupScheduler;
import com.example.dto.EmployeeFloorSummary;
import com.example.emailcontroller.Maincontroller;
import com.example.emailservice.Emailservices;
import com.example.emailscheduler.EmailScheduler;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaincontrollerTest {

    @InjectMocks
    private Maincontroller controller;

    @Mock private Emailservices emailService;
    @Mock private EmployeeSwipeRepository repository;
    @Mock private PdfReportGenerator pdfReportGenerator;
    @Mock private EmailScheduler scheduler;
    @Mock private SiteBackupScheduler backupScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ /send-tower-summary - success")
    void testSendTowerSummaryEmail_success() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});
        when(scheduler.getRecipients()).thenReturn(List.of("test@example.com"));

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("✅"));
        verify(emailService).sendWithAttachment(any(), any(), any(), any());
    }

    @Test
    @DisplayName("⚠️ /send-tower-summary - no data")
    void testSendTowerSummaryEmail_noData() {
        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(Collections.emptyList());

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("⚠️"));
    }

    @Test
    @DisplayName("❌ /send-tower-summary - invalid email")
    void testSendTowerSummaryEmail_invalidEmail() throws Exception {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);
        when(pdfReportGenerator.generateTowerSummaryPdf(anyList(), any())).thenReturn(new byte[]{1});
        when(scheduler.getRecipients()).thenReturn(List.of("invalid-email"));

        String result = controller.sendTowerSummaryEmail();
        assertTrue(result.contains("❌"));
    }

    @Test
    @DisplayName("✅ /cron/toggle")
    void testToggleCron() {
        ResponseEntity<String> response = controller.toggleCron(true);
        assertEquals("Cron job enabled", response.getBody());
        verify(scheduler).setCronEnabled(true);
    }

    @Test
    @DisplayName("✅ /recipients/add")
    void testAddRecipient() {
        ResponseEntity<String> response = controller.addRecipient("test@example.com");
        assertEquals("Added recipient: test@example.com", response.getBody());
        verify(scheduler).addRecipient("test@example.com");
    }

    @Test
    @DisplayName("✅ /recipients/remove")
    void testRemoveRecipient() {
        ResponseEntity<String> response = controller.removeRecipient("test@example.com");
        assertEquals("Removed recipient: test@example.com", response.getBody());
        verify(scheduler).removeRecipient("test@example.com");
    }

    @Test
    @DisplayName("✅ /recipients - list")
    void testListRecipients() {
        when(scheduler.getRecipients()).thenReturn(List.of("a@example.com", "b@example.com"));

        ResponseEntity<List<String>> response = controller.listRecipients();
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("a@example.com"));
    }

    @Test
    @DisplayName("✅ /summary - returns parsed DTO")
    void testGetSummary() {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);

        ResponseEntity<List<EmployeeFloorSummary>> response = controller.getSummary("2025-10-13");
        assertFalse(response.getBody().isEmpty());
        assertEquals("E001", response.getBody().get(0).getEmployeeId());
    }

    @Test
    @DisplayName("✅ /summary/csv - returns CSV bytes")
    void testGetCsvReport() {
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(new Object[]{1, "E001", "John", "Manager", 1L, 2L, 3L, 4L, 5L});

        when(repository.getTowerWiseSummaryBetween(any(), any(), eq("MVL"))).thenReturn(rawResults);

        ResponseEntity<byte[]> response = controller.getCsvReport("2025-10-13");
        String csv = new String(response.getBody());
        assertTrue(csv.contains("Employee ID"));
        assertTrue(csv.contains("John"));
    }

    @Test
    @DisplayName("✅ /backup/trigger")
    void testTriggerBackup() {
        when(backupScheduler.triggerBackupManually()).thenReturn("✅ Backup completed");

        ResponseEntity<String> response = controller.triggerBackup();
        assertEquals("✅ Backup completed", response.getBody());
    }
}











/*package com.example.sendexceldatatoemail;

import com.example.backup.SiteBackupScheduler;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MaincontrollerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private Emailservices emailService;
    @MockBean private EmployeeSwipeRepository repository;
    @MockBean private PdfReportGenerator pdfReportGenerator;
    @MockBean private SiteBackupScheduler backupScheduler;

    private final LocalDateTime start = LocalDateTime.of(2025, 10, 13, 0, 0);
    private final LocalDateTime end = start.plusDays(1);

    @Test
    void testSendTowerSummaryEmail_success() throws Exception {
        String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));

        Object[] row = new Object[] {
            0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L
        };
        
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(row);

        byte[] mockPdf = "PDF".getBytes();

        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString())).thenReturn(rawResults);
        Mockito.when(pdfReportGenerator.generateTowerSummaryPdf(Mockito.anyList(), eq(start.toLocalDate()))).thenReturn(mockPdf);
        Mockito.doNothing().when(emailService).sendWithAttachment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(get("/api/send-tower-summary"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Tower-wise report for " + formattedDate)));
    }

    @Test
    void testSendTowerSummaryEmail_noDataFound() throws Exception {
        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/send-tower-summary"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("⚠️ No tower-wise data found")));
    }

    @Test
    void testSendTowerSummaryEmail_exceptionThrown() throws Exception {
        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString()))
            .thenThrow(new RuntimeException("Simulated failure"));

        mockMvc.perform(get("/api/send-tower-summary"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("❌ Failed to generate or send report")));
    }

    @Test
    void testBackupStatusEndpoint() throws Exception {
        Mockito.when(backupScheduler.getLastBackupTime()).thenReturn("22 Oct 2025 02:00:00");

        mockMvc.perform(get("/api/backup/status"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("🕒 Last backup: 22 Oct 2025")));
    }

    @Test
    void testBackupTriggerEndpoint() throws Exception {
        Mockito.when(backupScheduler.triggerBackupManually()).thenReturn("✅ Backup triggered successfully at 22 Oct 2025 13:45:12");

        mockMvc.perform(post("/api/backup/trigger"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("✅ Backup triggered successfully")));
    }
}


*/










































/*
 * package com.example.sendexceldatatoemail; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import org.junit.jupiter.api.Test;
 * import org.mockito.Mockito; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.test.context.SpringBootTest; import
 * org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 * import org.springframework.boot.test.mock.mockito.MockBean; import
 * org.springframework.test.context.TestPropertySource; import
 * org.springframework.test.web.servlet.MockMvc;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.List;
 * 
 * import static org.mockito.ArgumentMatchers.anyString; import static
 * org.mockito.ArgumentMatchers.eq; import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import
 * static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 * 
 * @SpringBootTest
 * 
 * @AutoConfigureMockMvc
 * 
 * @TestPropertySource(locations = "classpath:application-test.properties")
 * public class MaincontrollerTest {
 * 
 * @Autowired private MockMvc mockMvc;
 * 
 * @MockBean private Emailservices emailService;
 * 
 * @MockBean private EmployeeSwipeRepository repository;
 * 
 * @MockBean private PdfReportGenerator pdfReportGenerator;
 * 
 * 
 * private final LocalDateTime start = LocalDateTime.of(2025, 10, 17, 0, 0);
 * private final LocalDateTime end = start.plusDays(1);
 * 
 * @Test void testSendTowerSummaryEmail_success() throws Exception { String
 * formattedDate =
 * start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
 * 
 * Object[] row = new Object[] { 0, "E001", "John Doe", "Security", 10L, 12L,
 * 8L, 9L, 11L }; List<Object[]> rawResults = List.of(row); byte[] mockPdf =
 * "PDF".getBytes();
 * 
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())).thenReturn(rawResults);
 * Mockito.when(pdfReportGenerator.generateTowerSummaryPdf(Mockito.anyList(),
 * eq(start.toLocalDate()))).thenReturn(mockPdf);
 * Mockito.doNothing().when(emailService).sendWithAttachment(Mockito.any(),
 * Mockito.any(), Mockito.any(), Mockito.any());
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("Tower-wise report for " + formattedDate))); }
 * 
 * @Test void testSendTowerSummaryEmail_noDataFound() throws Exception {
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())).thenReturn(List.of());
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("⚠️ No tower-wise data found"))); }
 * 
 * @Test void testSendTowerSummaryEmail_exceptionThrown() throws Exception {
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())) .thenThrow(new RuntimeException("Simulated failure"));
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("❌ Failed to generate or send report"))); } }
 */
/*
 * @Test void testBackupStatusEndpoint() throws Exception {
 * Mockito.when(backupScheduler.getLastBackupTime()).
 * thenReturn("22 Oct 2025 02:00:00");
 * 
 * mockMvc.perform(get("/api/backup/status")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("🕒 Last backup: 22 Oct 2025"))); }
 * 
 * @Test void testBackupTriggerEndpoint() throws Exception {
 * Mockito.when(backupScheduler.triggerBackupManually()).
 * thenReturn("✅ Backup triggered successfully at 22 Oct 2025 13:45:12");
 * 
 * mockMvc.perform(post("/api/backup/trigger")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("✅ Backup triggered successfully"))); } }
 */





















/*
 * package com.example.sendexceldatatoemail;
 * 
 * import com.example.dto.EmployeeFloorSummary; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import org.junit.jupiter.api.Test;
 * import org.mockito.Mockito; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.test.context.SpringBootTest; import
 * org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 * import org.springframework.boot.test.mock.mockito.MockBean; import
 * org.springframework.test.context.TestPropertySource; import
 * org.springframework.test.web.servlet.MockMvc;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.ArrayList; import java.util.List;
 * 
 * import static org.mockito.ArgumentMatchers.anyString; import static
 * org.mockito.ArgumentMatchers.eq; import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 * 
 * @SpringBootTest
 * 
 * @AutoConfigureMockMvc
 * 
 * @TestPropertySource(locations = "classpath:application-test.properties")
 * public class MaincontrollerTest {
 * 
 * @Autowired private MockMvc mockMvc;
 * 
 * @MockBean private Emailservices emailService;
 * 
 * @MockBean private EmployeeSwipeRepository repository;
 * 
 * @MockBean private PdfReportGenerator pdfReportGenerator;
 * 
 * private final LocalDateTime start = LocalDateTime.of(2025, 10, 17, 0, 0);
 * private final LocalDateTime end = start.plusDays(1);
 * 
 * @Test void testSendTowerSummaryEmail_success() throws Exception { String
 * formattedDate =
 * start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
 * 
 * Object[] row = new Object[] { 0, "E001", "John Doe", "Security", 10L, 12L,
 * 8L, 9L, 11L }; List<Object[]> rawResults = new ArrayList<>();
 * rawResults.add(row);
 * 
 * EmployeeFloorSummary mapped = new EmployeeFloorSummary( (Integer) row[0],
 * (String) row[1], (String) row[2], (String) row[3], ((Number)
 * row[4]).longValue(), ((Number) row[5]).longValue(), ((Number)
 * row[6]).longValue(), ((Number) row[7]).longValue(), ((Number)
 * row[8]).longValue() ); mapped.setTotal(50L);
 * 
 * byte[] mockPdf = "PDF".getBytes();
 * 
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())) .thenReturn(rawResults);
 * 
 * Mockito.when(pdfReportGenerator.generateTowerSummaryPdf(eq(List.of(mapped)),
 * eq(start.toLocalDate()))) .thenReturn(mockPdf);
 * 
 * Mockito.doNothing().when(emailService).sendWithAttachment(Mockito.any(),
 * Mockito.any(), Mockito.any(), Mockito.any());
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("Tower-wise report for " + formattedDate))); }
 * 
 * @Test void testSendTowerSummaryEmail_noDataFound() throws Exception {
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())) .thenReturn(List.of());
 * 
 * String expectedDate = start.toLocalDate().toString();
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("⚠️ No tower-wise data found for MVL on " + expectedDate)));
 * // ✅ Fixed casing }
 * 
 * @Test void testSendTowerSummaryEmail_exceptionThrown() throws Exception {
 * Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end),
 * anyString())) .thenThrow(new RuntimeException("Simulated failure"));
 * 
 * mockMvc.perform(get("/api/send-tower-summary")) .andExpect(status().isOk())
 * .andExpect(content().string(org.hamcrest.Matchers.
 * containsString("❌ Failed to generate or send report"))); } }
 */