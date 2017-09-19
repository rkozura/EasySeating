package com.kozu.easyseating.resolver;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.utils.Array;
import com.kozu.easyseating.object.Person;

import static com.kozu.easyseating.AndroidLauncher.MY_PERMISSIONS_REQUEST_READ_CONTACTS;

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
    public Array<Person> getPersonList() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return readPersonList();
        }

       return new Array<>();
    }

    public Array<Person> readPersonList() {
        Array<Person> personList = new Array<Person>();

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
