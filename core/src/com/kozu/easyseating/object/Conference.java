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
    private List<Table> tables;
    public List<Person> persons;

    public Conference() {
        tables = new ArrayList<Table>();
        persons = new ArrayList<Person>();
    }

    public List<Table> getTables() {
        return tables;
    }

    public void addTable(Table table) {
        tables.add(table);
        table.tableIdentifier = String.valueOf(tables.size());
    }

    public void removeTable(Table table) {
        tables.remove(table);

        for(int i = 0; i<tables.size(); i++) {
            tables.get(i).tableIdentifier = String.valueOf(i+1);
        }
    }
}
