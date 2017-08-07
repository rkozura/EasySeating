package com.kozu.easyseating.logic;

import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import java.util.Random;

import aurelienribon.tweenengine.Tween;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingLogic {
    public Conference conference;

    public Table selectedTable;
    public Table tappedTable;

    public SeatingLogic() {
        conference = new Conference();
    }

    public Table getTableAtPosition(Vector3 pos) {
        Table returnTable = null;

        for(Table table : conference.tables) {
            if(table.bounds.contains(pos.x, pos.y)) {
                returnTable = table;
                break;
            }
        }

        return returnTable;
    }

    public Person createPerson(String name) {
        Person person = new Person(name);
        conference.persons.add(person);

        return person;
    }

    public void removePerson(Person person) {
        conference.persons.remove(person);
    }

    public void addPersonToTable(Table table, Person person) {
        //Find the person in an existing table and remove
        for(Table allTable : conference.tables) {
            if(allTable.assignedSeats.contains(person)) {
                removePersonFromTable(allTable, person);
                break;
            }
        }

        //Find the next seat position for this table
        //TODO we can change this to use size of assignedseats instead of looping through assignedseats
        double nextSeatAngle = 0;
        for (Person personTemp : table.assignedSeats) {
            nextSeatAngle += 30;
        }

        double nextSeatRadians = Math.toRadians(nextSeatAngle);
        float x = (float) (table.bounds.radius * Math.cos(nextSeatRadians)) + table.bounds.x;
        float y = (float) (table.bounds.radius * Math.sin(nextSeatRadians)) + table.bounds.y;

        person.position.set(x, y);
        table.assignedSeats.add(person);
    }

    public void removePersonFromTable(Table table, Person person) {
        if(!table.assignedSeats.isEmpty()) {
            table.assignedSeats.remove(person);
        }

        setPersonPositions(table, new Vector3(table.bounds.x, table.bounds.y, 0));
    }

    public void addTableAtPosition(Vector3 pos) {
        Table table = new Table(pos.x, pos.y);
        conference.tables.add(table);
    }

    public void moveTableToPosition(Table table, Vector3 pos) {
        Tween.to(table, EntityAccessor.POSITION_XY, .2f).target(pos.x,pos.y)
                .start(TweenUtil.getTweenManager());

        //Re-position the people as well
        setPersonPositions(table, pos);
    }

    private void setPersonPositions(Table table, Vector3 pos) {
        double nextSeatAngle = 0;
        for(Person person : table.assignedSeats) {
            double nextSeatRadians = Math.toRadians(nextSeatAngle);
            float x = (float) (table.bounds.radius * Math.cos(nextSeatRadians)) + pos.x;
            float y = (float) (table.bounds.radius * Math.sin(nextSeatRadians)) + pos.y;

            //Delay the movement of people for effect
            Tween.to(person, EntityAccessor.POSITION_XY, .2f).target(x,y)
                    .delay(new Random().nextFloat() * (.3f - .05f) + .05f)
                    .start(TweenUtil.getTweenManager());

            nextSeatAngle += 30;
        }
    }
}
