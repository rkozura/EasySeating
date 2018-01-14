package com.kozu.easyseating.resolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rob on 1/13/2018.
 */

public class AndroidPDFGenerator implements PDFGenerator {
    Context context;

    public AndroidPDFGenerator(Context context) {
        this.context = context;
    }

    @Override
    public void generatePDF(Conference conference) {
        File file = new File(context.getFilesDir(), replaceSpacesWithUnderscores(conference.conferenceName)+"_"+generateUniqueFileIndeitifierString()+".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);

            PdfDocument document = new PdfDocument();

            // crate a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int)conference.conferenceWidth, (int)conference.conferenceHeight, 1).create();

            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // draw something on the page
            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setColor(Color.RED);

            //Paint the tables
            for(Table table : conference.getTables()) {
                canvas.drawCircle(table.getX(), (int)conference.conferenceHeight-table.getY(), table.getRadius(), paint);
            }

            paint.setColor(Color.BLACK);
            for(Table table : conference.getTables()) {
                canvas.drawText(table.tableIdentifier, table.getX(), (int)conference.conferenceHeight-table.getY(), paint);
            }

            // finish the page
            document.finishPage(page);

            // add more pages

            // write the document content
            document.writeTo(fos);

            // close the document
            document.close();

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkURI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext()
                            .getPackageName() + ".provider", file);
            install.setDataAndType(apkURI, "application/pdf");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(install);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("Saving android!");
//        File file = new File(context.getFilesDir(), "test.pdf");
//        try {
//            document.save(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private String generateUniqueFileIndeitifierString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    private String replaceSpacesWithUnderscores(String conferenceName) {
        return conferenceName.replaceAll(" ", "_").toLowerCase();
    }
}
