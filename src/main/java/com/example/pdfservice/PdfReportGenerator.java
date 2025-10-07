package com.example.pdfservice;

import org.springframework.stereotype.Component;
import com.example.dto.EmployeeFloorSummary;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class PdfReportGenerator {

    // ✅ Centralized font size
    private static final int BASE_FONT_SIZE = 10;

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE + 2, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE - 1, Font.BOLD);
    private static final Font DATA_FONT = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE);
    private static final Font TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE + 2, Font.BOLD);
    private static final Font LEGEND_FONT = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE - 1, Font.BOLD);

    private static final String[] COLUMNS = {
        "S.No", "Employee ID", "Name", "Designation", "A", "B", "C", "D", "E", "Total"
    };

    public byte[] generateTowerSummaryPdf(List<EmployeeFloorSummary> summaries, LocalDate date) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String headerText = "RFID Punching Report – MVL on " + formattedDate;

        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new PdfHeaderEvent(headerText, TITLE_FONT)); // ✅ Repeats heading on every page

        document.open();

        PdfPTable table = new PdfPTable(COLUMNS.length);
        table.setWidthPercentage(95);
        table.setSpacingBefore(180f); // ✅ Balanced spacing below heading
        table.setWidths(new float[]{1.2f, 2f, 3.5f, 3.5f, 1f, 1f, 1f, 1f, 1f, 2f});

        // Header row
        for (String header : COLUMNS) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorderWidthBottom(1f);
            table.addCell(cell);
        }

        table.setHeaderRows(1); // ✅ Repeat header on every page

        summaries.sort(Comparator.comparingLong(EmployeeFloorSummary::getTotal));

        int serial = 1;
        long totalPunches = 0;

        for (EmployeeFloorSummary summary : summaries) {
            boolean isLowPunch = summary.getTotal() < 50;

            Object[] values = {
                serial++,
                summary.getEmployeeId(),
                summary.getEmployeeName(),
                summary.getDesignation(),
                summary.getTowerA(),
                summary.getTowerB(),
                summary.getTowerC(),
                summary.getTowerD(),
                summary.getTowerE(),
                summary.getTotal()
            };

            totalPunches += summary.getTotal();

            for (Object value : values) {
                String text = value == null ? "" : String.valueOf(value);

                Font font = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE);
                if (isLowPunch) {
                    font.setStyle(Font.BOLD);
                    font.setColor(new BaseColor(255, 102, 102)); // Red
                }

                PdfPCell cell = new PdfPCell(new Phrase(text, font));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.WHITE);
                cell.setPadding(3f);
                table.addCell(cell);
            }
        }

        // Final row: Overall Total
        PdfPCell labelCell = new PdfPCell(new Phrase("Total", TOTAL_FONT));
        labelCell.setColspan(9);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setBackgroundColor(BaseColor.YELLOW);
        labelCell.setPadding(3f);
        table.addCell(labelCell);

        PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(totalPunches), TOTAL_FONT));
        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        totalCell.setBackgroundColor(BaseColor.YELLOW);
        totalCell.setPadding(3f);
        table.addCell(totalCell);

        document.add(table);

        // Separator line
        LineSeparator separator = new LineSeparator();
        separator.setOffset(-2);
        separator.setLineColor(BaseColor.DARK_GRAY);
        document.add(separator);

        // Legend message
        Paragraph legend = new Paragraph();
        legend.setSpacingBefore(10f);
        legend.setAlignment(Element.ALIGN_CENTER);

        Font redFont = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE - 1, Font.BOLD, new BaseColor(255, 102, 102));
        Font grayFont = new Font(Font.FontFamily.HELVETICA, BASE_FONT_SIZE - 1, Font.BOLD, new BaseColor(120, 120, 120));

        legend.add(new Chunk("• ", LEGEND_FONT));
        legend.add(new Chunk("Red", redFont));
        legend.add(new Chunk(" indicates low punching activity.\n", LEGEND_FONT));

        legend.add(new Chunk("• ", LEGEND_FONT));
        legend.add(new Chunk("Gray", grayFont));
        legend.add(new Chunk(" represents average or normal punching activity.", LEGEND_FONT));

        document.add(legend);

        document.close();
        return out.toByteArray();
    }

    private boolean isNumeric(String text) {
        if (text == null || text.isEmpty()) return false;
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
