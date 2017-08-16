package com.kozu.easyseating.pdf;

import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Table;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;

/**
 * Created by Rob on 8/14/2017.
 */

public class PDFGenerator {
    private SeatingLogic seatingLogic;

    public PDFGenerator(SeatingLogic seatingLogic) {
        this.seatingLogic = seatingLogic;
    }
    public void generatePDF() {
        int line = 0;

        String outputFileName = "C://Users//Rob//Desktop//Simple.pdf";
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(PDRectangle.A4);
        // PDPage.PAGE_SIZE_LETTER is also possible
        PDRectangle rect = page1.getMediaBox();
        System.out.println(rect);
        // rect can be used to get the page width and height
        document.addPage(page1);

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDPageContentStream cos = null;
        // Start a new content stream which will "hold" the to be created content
        try {
            cos = new PDPageContentStream(document, page1);

            float ratio = rect.getWidth()/seatingLogic.conference.conferenceWidth;

            for(Table table : seatingLogic.conference.getTables()) {
                drawCircle(cos, table.getX()*ratio, table.getY()*ratio, 70*ratio, Color.GRAY);
            }
            //int numberOfCellsY = Math.round(NUMBER_OF_BOXES * 1.4141f);
//            cos.beginText();
//            cos.setFont(fontPlain, 12);
//            cos.moveTextPositionByAmount(100, rect.getHeight() - 50 * (++line));
//            cos.drawString("Hello World");
//            cos.endText();

            // Save the results and ensure that the document is properly closed:

            cos.close();
            document.save(outputFileName);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawCircle(PDPageContentStream contentStream, float cx, float cy, float r, Color color) throws IOException {
        final float k = 0.552284749831f;
        contentStream.setNonStrokingColor(color);
        contentStream.moveTo(cx - r, cy);
        contentStream.curveTo(cx - r, cy + k * r, cx - k * r, cy + r, cx, cy + r);
        contentStream.curveTo(cx + k * r, cy + r, cx + r, cy + k * r, cx + r, cy);
        contentStream.curveTo(cx + r, cy - k * r, cx + k * r, cy - r, cx, cy - r);
        contentStream.curveTo(cx - k * r, cy - r, cx - r, cy - k * r, cx - r, cy);
        contentStream.fill();
    }
}
