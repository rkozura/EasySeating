package com.kozu.easyseating.resolver;

import com.kozu.easyseating.object.Person;

import java.util.ArrayList;

/**
 * Created by Rob on 8/31/2017.
 */

public interface PersonImporter {
    void checkPermission();
    ArrayList<Person> getPersonList();
}
