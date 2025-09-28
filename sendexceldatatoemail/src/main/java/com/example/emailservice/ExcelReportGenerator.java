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
        long grandTotal = 0;

        //  Header style
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //  Data style
        XSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        //  Highlight style for total < 7
        XSSFCellStyle highlightStyle = workbook.createCellStyle();
        highlightStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 230, 230), null));
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        highlightStyle.setAlignment(HorizontalAlignment.CENTER);

        //  Header row
        int rowNum = 0;
        String[] columns = {"S_No", "Employee ID", "Name", "Designation", "Floor F1", "Floor F2", "Floor F3", "Floor F4", "Floor F5","Floor F6","Floor F7","Floor B", "Total"};
        Row header = sheet.createRow(rowNum++);
        for (int i = 0; i < columns.length; i++) {
            XSSFCell cell = (XSSFCell) header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        //  Sort summaries by total (ascending)
        summaries.sort((a, b) -> {
            long totalA = a.getFloorF1() + a.getFloorF2() + a.getFloorF3() + a.getFloorF4() + a.getFloorF5() +a.getFloorF6() +a.getFloorF7() +a.getFloorB();
            long totalB = b.getFloorF1() + b.getFloorF2() + b.getFloorF3() + b.getFloorF4() + b.getFloorF5() +a.getFloorF6() +a.getFloorF7() +a.getFloorB();
            return Long.compare(totalA, totalB);
        });

        // Data rows
        for (EmployeeFloorSummary summary : summaries) {
            Row row = sheet.createRow(rowNum++);
            long total = summary.getFloorF1() + summary.getFloorF2() + summary.getFloorF3()
                       + summary.getFloorF4() + summary.getFloorF5()+summary.getFloorF6() +summary.getFloorF7() +summary.getFloorB();
            
            grandTotal =grandTotal + total;

            Object[] values = {
                summary.getsNo(), summary.getEmployeeId(), summary.getEmployeeName(), summary.getDesignation(),
                summary.getFloorF1(), summary.getFloorF2(), summary.getFloorF3(),
                summary.getFloorF4(), summary.getFloorF5(),summary.getFloorF6(),summary.getFloorF7(),summary.getFloorB(), total
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
        Row totalRow = sheet.createRow(rowNum++);
        XSSFCellStyle totalStyle = workbook.createCellStyle();
        XSSFFont totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setAlignment(HorizontalAlignment.CENTER);

        // Label cell
        XSSFCell labelCell = (XSSFCell) totalRow.createCell(columns.length - 2); // Second last column
        labelCell.setCellValue("Overall Total");
        labelCell.setCellStyle(totalStyle);

        // Total value cell
        XSSFCell valueCell = (XSSFCell) totalRow.createCell(columns.length - 1); // Last column
        valueCell.setCellValue(grandTotal);
        valueCell.setCellStyle(totalStyle);


        //  Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        //  Write to memory (no file saved)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray(); //  Ready for email attachment
    }
}
