package com.example.pdfservice;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PdfHeaderEvent extends PdfPageEventHelper {

    private final String headerText;
    private final Font headerFont;

    public PdfHeaderEvent(String headerText, Font headerFont) {
        this.headerText = headerText;
        this.headerFont = headerFont;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();

        float centerX = (document.right() + document.left()) / 2;
        float topY = document.top() + document.topMargin(); // Top edge + margin
        float headerY = topY - 17; // Adjust this value if needed

        Phrase header = new Phrase(headerText, headerFont);
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, header, centerX, headerY, 0);

        
        Phrase pageNumber = new Phrase("Page " + writer.getPageNumber(), new Font(Font.FontFamily.HELVETICA, 8));
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, pageNumber, document.right(), headerY, 0);
    }
}