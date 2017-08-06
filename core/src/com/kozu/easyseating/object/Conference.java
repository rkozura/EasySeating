package com.kozu.easyseating.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/3/2017.
 */

/**
 * A conference is where tables are setup at
 */
public class Conference {
    public List<Table> tables;   //The tables
    public List<Person> persons; //The people

    public Conference() {
        tables = new ArrayList<Table>();
        persons = new ArrayList<Person>();
    }
}
