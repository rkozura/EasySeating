package com.kozu.easyseating.desktop.resolver;

import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.resolver.PersonImporter;

import java.util.ArrayList;

/**
 * Created by Rob on 8/31/2017.
 */

public class DesktopPersonImporter implements PersonImporter {
    @Override
    public void checkPermission() {
        //No need on desktop
    }

    @Override
    public ArrayList<Person> getPersonList() {
        ArrayList<Person> people = new ArrayList<Person>();
        people.add(new Person("Desktop ImporterOne"));
        people.add(new Person("Desktop ImporterTwo"));
        people.add(new Person("Desktop ImporterThree"));
        people.add(new Person("Desktop ImporterFour"));
        people.add(new Person("Desktop ImporterFive"));
        people.add(new Person("Desktop ImporterSix"));
        people.add(new Person("Desktop ImporterSeven"));
        people.add(new Person("Desktop ImporterEight"));
        people.add(new Person("Desktop ImporterNine"));

        return people;
    }
}
