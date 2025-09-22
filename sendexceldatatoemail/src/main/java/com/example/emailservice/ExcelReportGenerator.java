package com.example.emailservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import com.example.dto.EmployeeFloorSummary;

@Component
public class ExcelReportGenerator {

    public byte[] generateFloorReport(List<EmployeeFloorSummary> summaries) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Floor Swipe Summary");

        // ✅ Header style
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ✅ Data style
        XSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        // ✅ Highlight style for total < 7
        XSSFCellStyle highlightStyle = workbook.createCellStyle();
        highlightStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 230, 230), null));
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        highlightStyle.setAlignment(HorizontalAlignment.CENTER);

        // ✅ Header row
        int rowNum = 0;
        String[] columns = {"Employee ID", "Name", "Designation", "Floor A", "Floor B", "Floor C", "Floor D", "Floor E", "Total"};
        Row header = sheet.createRow(rowNum++);
        for (int i = 0; i < columns.length; i++) {
            XSSFCell cell = (XSSFCell) header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ Sort summaries by total (ascending)
        summaries.sort((a, b) -> {
            long totalA = a.getFloorA() + a.getFloorB() + a.getFloorC() + a.getFloorD() + a.getFloorE();
            long totalB = b.getFloorA() + b.getFloorB() + b.getFloorC() + b.getFloorD() + b.getFloorE();
            return Long.compare(totalA, totalB);
        });

        // ✅ Data rows
        for (EmployeeFloorSummary summary : summaries) {
            Row row = sheet.createRow(rowNum++);
            long total = summary.getFloorA() + summary.getFloorB() + summary.getFloorC()
                       + summary.getFloorD() + summary.getFloorE();

            Object[] values = {
                summary.getEmployeeId(), summary.getEmployeeName(), summary.getDesignation(),
                summary.getFloorA(), summary.getFloorB(), summary.getFloorC(),
                summary.getFloorD(), summary.getFloorE(), total
            };

            for (int i = 0; i < values.length; i++) {
                XSSFCell cell = (XSSFCell) row.createCell(i);
                if (values[i] instanceof String) {
                    cell.setCellValue((String) values[i]);
                } else if (values[i] instanceof Number) {
                    cell.setCellValue(((Number) values[i]).longValue());
                }
                cell.setCellStyle(total < 7 ? highlightStyle : dataStyle);
            }
        }

        // ✅ Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ✅ Write to memory (no file saved)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray(); // ✅ Ready for email attachment
    }
}
