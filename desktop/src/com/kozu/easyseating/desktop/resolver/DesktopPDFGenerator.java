package com.kozu.easyseating.desktop.resolver;

import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.resolver.PDFGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;

/**
 * Created by Rob on 1/13/2018.
 */

public class DesktopPDFGenerator implements PDFGenerator {
    @Override
    public void generatePDF(Conference conference) {
        //System.out.println("Desktop saving PDF");

        int line = 0;

        String outputFileName = "C://Users//Rob//Desktop//Simple.pdf";
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(PDRectangle.A4);
        // PDPage.PAGE_SIZE_LETTER is also possible
        PDRectangle rect = page1.getMediaBox();

        // rect can be used to get the page width and height
        document.addPage(page1);

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDPageContentStream cos = null;
        // Start a new content stream which will "hold" the to be created content
        try {
            cos = new PDPageContentStream(document, page1);

            double ratio = rect.getWidth()/conference.conferenceWidth;

            for(Table table : conference.getTables()) {
                drawCircle(cos, (float)(table.getX()*ratio), (float)(table.getY()*ratio), (float)(70*ratio), Color.GRAY);

                cos.beginText();
                cos.newLineAtOffset((float)(table.getX()*ratio), (float)(table.getY()*ratio));
                cos.setFont( fontPlain, 12 );
                cos.setNonStrokingColor(Color.BLACK);
                cos.showText(table.tableIdentifier);
                cos.endText();
            }

            // Save the results and ensure that the document is properly closed:
            cos.close();
            document.save(outputFileName);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void drawCircle(PDPageContentStream contentStream, float cx, float cy, float r, Color color) throws IOException {
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
