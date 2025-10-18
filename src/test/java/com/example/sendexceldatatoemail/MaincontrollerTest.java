package com.example.sendexceldatatoemail;

import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class MaincontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Emailservices emailService;

    @MockBean
    private EmployeeSwipeRepository repository;

    @MockBean
    private PdfReportGenerator pdfReportGenerator;

    private final LocalDateTime start = LocalDateTime.of(2025, 10, 12, 0, 0);
    private final LocalDateTime end = start.plusDays(1);

    
    @Test
    void testSendTowerSummaryEmail_success() throws Exception {
        String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));

        Object[] row = new Object[] {
            0, "E001", "John Doe", "Security", 10L, 12L, 8L, 9L, 11L
        };
        //List<Object[]> rawResults = (List<Object[]>) (List<?>) List.of(row);
        
        List<Object[]> rawResults = new ArrayList<>();
        rawResults.add(row);


        EmployeeFloorSummary mapped = new EmployeeFloorSummary(
            (Integer) row[0],
            (String) row[1],
            (String) row[2],
            (String) row[3],
            ((Number) row[4]).longValue(),
            ((Number) row[5]).longValue(),
            ((Number) row[6]).longValue(),
            ((Number) row[7]).longValue(),
            ((Number) row[8]).longValue()
        );
        mapped.setTotal(50L);

        byte[] mockPdf = "PDF".getBytes();

        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString()))
                .thenReturn(rawResults);

        Mockito.when(pdfReportGenerator.generateTowerSummaryPdf(eq(List.of(mapped)), eq(start.toLocalDate())))
                .thenReturn(mockPdf);

        Mockito.doNothing().when(emailService).sendWithAttachment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(get("/api/send-tower-summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tower-wise report for " + formattedDate)));
    }

    @Test
    void testSendTowerSummaryEmail_noDataFound() throws Exception {
        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString()))
                .thenReturn(List.of());

        String expectedDate = start.toLocalDate().toString();

        mockMvc.perform(get("/api/send-tower-summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("⚠️ No tower-wise data found for Mvl on " + expectedDate)));
    }

    @Test
    void testSendTowerSummaryEmail_exceptionThrown() throws Exception {
        Mockito.when(repository.getTowerWiseSummaryBetween(eq(start), eq(end), anyString()))
                .thenThrow(new RuntimeException("Simulated failure"));

        mockMvc.perform(get("/api/send-tower-summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("❌ Failed to generate or send report")));
    }
}
