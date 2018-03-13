package com.kozu.easyseating.resolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.ui.PeopleListAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Rob on 1/13/2018.
 */

public class AndroidPDFGenerator implements PDFGenerator {
    Context context;
    private static final float TEXT_SIZE = 48f;
    private static final float PERSON_TEXT_SIZE = 35f;
    private static final float TITLE_SIZE = 100f;

    public AndroidPDFGenerator(Context context) {
        this.context = context;
    }

    @Override
    public void generatePDF(Conference conference) {
        File pdfPath = new File(context.getFilesDir().toString()+"/pdfs/");
        if(!pdfPath.mkdirs()) {
            for(File file: pdfPath.listFiles()) {
                file.delete();
            }
        }

        File file = new File(pdfPath, replaceSpacesWithUnderscores(conference.conferenceName)+"_"+generateUniqueFileIndeitifierString()+".pdf");
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

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            for(Table table : conference.getTables()) {
                canvas.drawCircle(table.getX(), (int)conference.conferenceHeight-table.getY(), table.getRadius(), paint);
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextSize(TEXT_SIZE);
            for(Table table : conference.getTables()) {
                drawTextCentred(canvas, paint, table.tableIdentifier, table.getX(), (int)conference.conferenceHeight-table.getY());
            }

            paint.setTextSize(TITLE_SIZE);
            drawTextCentred(canvas, paint, conference.conferenceName, canvas.getWidth()/2, 40);

            for(Table table : conference.getTables()) {
                for(Person person : table.assignedSeats) {
                    paint.setColor(Color.argb(255, 114, 229, 255));
                    canvas.drawCircle(person.getX(), (int)conference.conferenceHeight-person.getY(), person.bounds.radius, paint);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(PERSON_TEXT_SIZE);
                    drawTextCentred(canvas, paint, person.getTruncatedName(), person.getX(), (int)conference.conferenceHeight-person.getY());
                }
            }

            // finish the page
            document.finishPage(page);

            // crate a page description
            generateGuestListTable(document, paint, conference, 20, 20,(int)conference.conferenceWidth - 20, (int)conference.conferenceHeight - 20);

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

    private void generateGuestListTable(PdfDocument document, Paint paint, Conference conference, float left, float top, float right, float bottom) {
        ArrayList<Person> people = conference.persons;
        Collections.sort(people, new PeopleListAdapter.TableComparator<Person>());

        paint.setTextSize(TEXT_SIZE);
        float rows = 20;
        int numberOfPages = (int)Math.ceil(conference.persons.size()/rows);
        float gutterHeight = (bottom-top)/rows;

        Iterator<Person> jj = conference.persons.iterator();
        int i = 0;
        int personCount = 1;
        while(i < numberOfPages) {
            i++;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int)right+20, (int)bottom+20, i+2).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            //Draw the outline of the table
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawRect(left, top, right, bottom, paint);

            paint.setStyle(Paint.Style.FILL);
            //Draw the rows
            float y = top + gutterHeight;
            for(int j=0; j<rows; j++) {
                canvas.drawLine(left, y, right, y, paint);
                if(jj.hasNext()) {
                    Person person = jj.next();
                    if(person.assignedTable == null || person.assignedTable.isEmpty()) {
                        canvas.drawText(personCount+". UNASSIGNED "+person.getFirstName() + " " + person.getLastName(), left, y, paint);
                    } else {
                        canvas.drawText(personCount+". Table "+person.assignedTable+" "+person.getFirstName() + " " + person.getLastName(), left, y, paint);
                    }
                    personCount++;
                }
                y += gutterHeight;
            }

            document.finishPage(page);
        }
    }

    private String generateUniqueFileIndeitifierString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    private String replaceSpacesWithUnderscores(String conferenceName) {
        return conferenceName.replaceAll(" ", "_").toLowerCase();
    }

    private final Rect textBounds = new Rect(); //don't new this up in a draw method
    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy){
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }
}
