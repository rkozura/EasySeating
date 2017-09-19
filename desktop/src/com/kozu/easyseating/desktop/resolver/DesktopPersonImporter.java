package com.kozu.easyseating.desktop.resolver;

import com.badlogic.gdx.utils.Array;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.resolver.PersonImporter;

/**
 * Created by Rob on 8/31/2017.
 */

public class DesktopPersonImporter implements PersonImporter {
    @Override
    public Array<Person> getPersonList() {
        Array<Person> people = new Array<Person>();
        people.add(new Person("Desktop Importer1"));
        people.add(new Person("Desktop Importer2"));
        people.add(new Person("Desktop Importer3"));
        people.add(new Person("Desktop Importer4"));
        people.add(new Person("Desktop Importer5"));
        people.add(new Person("Desktop Importer6"));
        people.add(new Person("Desktop Importer7"));
        people.add(new Person("Desktop Importer8"));
        people.add(new Person("Desktop Importer9"));

        return people;
    }
}
