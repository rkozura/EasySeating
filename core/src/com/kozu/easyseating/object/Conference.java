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
    List<Table> tables;

    public Conference() {
        tables = new ArrayList<Table>();
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
