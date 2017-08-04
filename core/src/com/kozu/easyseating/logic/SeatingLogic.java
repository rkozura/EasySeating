package com.kozu.easyseating.logic;

import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingLogic {
    private Conference conference;
    private Table selectedTable;

    public SeatingLogic() {
        conference = new Conference();
    }

    /**
     * Handles a tap at the given x,y coord
     * @param pos
     */
    public void handleTap(Vector3 pos) {
        if(selectedTable != null) {
            selectedTable.bounds.setPosition(pos.x, pos.y);

            //Re-position the people
            double nextSeatAngle = 0;
            for(Person person : selectedTable.assignedSeats) {
                double nextSeatRadians = Math.toRadians(nextSeatAngle);
                float x = (float) (selectedTable.bounds.radius * Math.cos(nextSeatRadians)) + selectedTable.bounds.x;
                float y = (float) (selectedTable.bounds.radius * Math.sin(nextSeatRadians)) + selectedTable.bounds.y;

                person.position.set(x, y);

                nextSeatAngle += 30;
            }
        } else {
            boolean touchedTable = false;
            for (Table table : conference.getTables()) {
                //If a table was tapped, add a person to it
                if (table.bounds.contains(pos.x, pos.y)) {

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

                    touchedTable = true;
                }
            }

            if (!touchedTable) {
                Table table = new Table(pos.x, pos.y);
                conference.getTables().add(table);
            }
        }
    }

    public void handleLongPress(Vector3 pos) {
        for(Table table : conference.getTables()) {
            //If a table was long pressed, capture it for dragging/moving
            if(table.bounds.contains(pos.x, pos.y)) {
                selectedTable = table;
            } else {
                selectedTable = null;
            }
        }
    }

    public void update(float delta) {

    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }
}
