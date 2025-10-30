/*package com.example.sendexceldatatoemail;

import com.example.dto.EmployeeFloorSummary;
import com.example.pdfservice.PdfReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfReportGeneratorTest {

    private PdfReportGenerator generator;

    @BeforeEach
    void setup() {
        generator = new PdfReportGenerator();
    }

    @Test
    void testGenerateTowerSummaryPdf_withValidData() throws Exception {
        EmployeeFloorSummary summary = new EmployeeFloorSummary();
        summary.setEmployeeId("E001");
        summary.setEmployeeName("John Doe");
        summary.setDesignation("Security");
        summary.setTowerA(10L);
        summary.setTowerB(12L);
        summary.setTowerC(8L);
        summary.setTowerD(9L);
        summary.setTowerE(11L);
        summary.setTotal(50L);

        List<EmployeeFloorSummary> summaries = new ArrayList<>();
        summaries.add(summary);

        byte[] pdfBytes = generator.generateTowerSummaryPdf(summaries, LocalDate.of(2025, 10, 12));
        assertNotNull(pdfBytes, "PDF byte array should not be null");
        assertTrue(pdfBytes.length > 0, "PDF byte array should not be empty");
    }

    @Test
    void testGenerateTowerSummaryPdf_withEmptyList() throws Exception {
        List<EmployeeFloorSummary> summaries = new ArrayList<>();
        byte[] pdfBytes = generator.generateTowerSummaryPdf(summaries, LocalDate.of(2025, 10, 12));
        assertNotNull(pdfBytes, "PDF byte array should not be null");
        assertTrue(pdfBytes.length > 0, "PDF should still be generated even with no data");
    }

    @Test
    void testIsNumeric_validNumber() {
        assertTrue(generator.isNumeric("123.45"));
        assertTrue(generator.isNumeric("0"));
        assertTrue(generator.isNumeric("-99.99"));
    }

    @Test
    void testIsNumeric_invalidNumber() {
        assertFalse(generator.isNumeric("abc"));
        assertFalse(generator.isNumeric("12a"));
    }

    @Test
    void testIsNumeric_nullOrEmpty() {
        assertFalse(generator.isNumeric(null));
        assertFalse(generator.isNumeric(""));
    }
}

*/