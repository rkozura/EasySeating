package com.kozu.easyseating.logic;

import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingLogic {
    public Conference conference;
    public Table selectedTable;

    public SeatingLogic() {
        conference = new Conference();
    }

    public Table getTableAtPosition(Vector3 pos) {
        Table returnTable = null;

        for(Table table : conference.getTables()) {
            if(table.bounds.contains(pos.x, pos.y)) {
                returnTable = table;
                break;
            }
        }

        return returnTable;
    }

    public void addPersonToTable(Table table) {
        //Find the next seat position for this table
        double nextSeatAngle = 0;
        for (Person person : table.assignedSeats) {
            nextSeatAngle += 30;
        }

        double nextSeatRadians = Math.toRadians(nextSeatAngle);
        float x = (float) (table.bounds.radius * Math.cos(nextSeatRadians)) + table.bounds.x;
        float y = (float) (table.bounds.radius * Math.sin(nextSeatRadians)) + table.bounds.y;

        Person newPerson = new Person();
        newPerson.position.set(x, y);
        table.assignedSeats.add(newPerson);
    }


    public void addTableAtPosition(Vector3 pos) {
        Table table = new Table(pos.x, pos.y);
        conference.getTables().add(table);
    }

    public void moveTableToPosition(Table table, Vector3 pos) {
        //TODO add tweening :)
        table.bounds.setPosition(pos.x, pos.y);

        //Re-position the people as well
        double nextSeatAngle = 0;
        for(Person person : table.assignedSeats) {
            double nextSeatRadians = Math.toRadians(nextSeatAngle);
            float x = (float) (table.bounds.radius * Math.cos(nextSeatRadians)) + table.bounds.x;
            float y = (float) (table.bounds.radius * Math.sin(nextSeatRadians)) + table.bounds.y;

            person.position.set(x, y);

            nextSeatAngle += 30;
        }
    }
}
