package com.example.sendexceldatatoemail;

import com.example.pdfservice.PdfHeaderEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PdfHeaderEventTest {

    @Test
    void testHeaderEventRendersWithoutError() throws Exception {
        // Create a new PDF document
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Create header font and event
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfHeaderEvent headerEvent = new PdfHeaderEvent("Test Header", headerFont);

        // Attach header event to writer
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(headerEvent);

        // Write content to PDF
        document.open();
        document.add(new Paragraph("This is a test page."));
        document.newPage(); // triggers header rendering on second page
        document.add(new Paragraph("Second page content."));
        document.close();

        // Validate output
        byte[] pdfBytes = out.toByteArray();
        assertNotNull(pdfBytes, "PDF bytes should not be null");
        assertTrue(pdfBytes.length > 0, "PDF should be generated with header event");
    }
}
