package com.kozu.easyseating.resolver;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kozu.easyseating.object.Person;

import java.util.ArrayList;

/**
 * Created by Rob on 8/31/2017.
 */

public class AndroidPersonImporter implements PersonImporter {
    private Activity activity;
    private Context context;

    public AndroidPersonImporter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void checkPermission() {
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(activity)
                        .withTitle("Contacts permission")
                        .withMessage("Enabling Contacts Permission makes creating your guest list faster and easier!")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(dialogPermissionListener).check();
    }

    @Override
    public ArrayList<Person> getPersonList() {
        ArrayList<Person> contacts = null;

        checkPermission();

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            contacts = readPersonList();
        }

        return contacts;
    }

    public ArrayList<Person> readPersonList() {
        ArrayList<Person> personList = new ArrayList<Person>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                personList.add(new Person(name));
            }
        }

        return personList;
    }
}
