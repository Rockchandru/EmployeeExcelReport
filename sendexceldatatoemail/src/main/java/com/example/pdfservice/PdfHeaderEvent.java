package com.example.pdfservice;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfHeaderEvent extends PdfPageEventHelper {

    private static final Logger logger = LoggerFactory.getLogger(PdfHeaderEvent.class);

    private final String headerText;
    private final Font headerFont;

    public PdfHeaderEvent(String headerText, Font headerFont) {
        this.headerText = headerText;
        this.headerFont = headerFont;
        logger.info("PdfHeaderEvent initialized with header: '{}'", headerText);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();

        float centerX = (document.right() + document.left()) / 2;
        float topY = document.top() + document.topMargin();
        float headerY = topY - 17;

        Phrase header = new Phrase(headerText, headerFont);
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, header, centerX, headerY, 0);

        Phrase pageNumber = new Phrase("Page " + writer.getPageNumber(), new Font(Font.FontFamily.HELVETICA, 8));
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, pageNumber, document.right(), headerY, 0);

        logger.debug("Header and page number rendered for page {}", writer.getPageNumber());
    }
}
