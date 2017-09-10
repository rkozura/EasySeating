package com.kozu.easyseating.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import java.util.Random;

import aurelienribon.tweenengine.Tween;

/**
 * SeatingLogic contains all the operations one can do to the conference.  Also contains selected
 * and tapped tables for use by the controller to determine what happens to gestures
 *
 * Created by Rob on 8/2/2017.
 */
public class SeatingLogic {
    private static final float CONFERENCE_WIDTH = 1000f;
    private static final float CONFERENCE_HEIGHT = CONFERENCE_WIDTH * 1.5f;
    private static final int GRID_COUNT = 40;

    public Conference conference;

    public SeatingLogic(String conferenceName) {
        conference = new Conference(conferenceName, CONFERENCE_WIDTH, CONFERENCE_HEIGHT);

        //divide the area but the number of tiles to get the max area a tile could cover
        //this optimal size for a tile will more often than not make the tiles overlap, but
        //a tile can never be bigger than this size
        double maxSize = Math.sqrt((CONFERENCE_HEIGHT * CONFERENCE_WIDTH) / GRID_COUNT);
        //find the number of whole tiles that can fit into the height
        double numberOfPossibleWholeTilesH = Math.floor(CONFERENCE_HEIGHT / maxSize);
        //find the number of whole tiles that can fit into the width
        double numberOfPossibleWholeTilesW = Math.floor(CONFERENCE_WIDTH / maxSize);
        //works out how many whole tiles this configuration can hold
        double total = numberOfPossibleWholeTilesH * numberOfPossibleWholeTilesW;

        //if the number of number of whole tiles that the max size tile ends up with is less than the require number of
        //tiles, make the maxSize smaller and recaluate
        while(total < GRID_COUNT){
            maxSize--;
            numberOfPossibleWholeTilesH = Math.floor(CONFERENCE_HEIGHT / maxSize);
            numberOfPossibleWholeTilesW = Math.floor(CONFERENCE_WIDTH / maxSize);
            total = numberOfPossibleWholeTilesH * numberOfPossibleWholeTilesW;
        }

        for(double i=maxSize; i<=CONFERENCE_WIDTH-75; i+=maxSize) {
            for(double j=maxSize; j<=CONFERENCE_HEIGHT-75; j+=maxSize) {
                conference.snapGrid.put(new Vector2((float)i, (float)j), null);
            }
        }
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

    public Person createPerson(String name) {
        Person person = new Person(name);
        conference.persons.add(person);

        return person;
    }

    public void removePerson(Person person) {
        conference.persons.removeValue(person, false);
        for(Table table : conference.getTables()) {
            if(table.assignedSeats.contains(person)) {
                table.assignedSeats.remove(person);
                break;
            }
        }
    }

    public void addPersonToTable(Table table, Person person) {
        //Find the person in an existing table and remove
        for(Table allTable : conference.getTables()) {
            if(allTable.assignedSeats.contains(person)) {
                removePersonFromTable(allTable, person);
                break;
            }
        }

        table.assignedSeats.add(person);
        setPersonPositions(table, new Vector3(table.bounds.x, table.bounds.y, 0));
    }

    public void removePersonFromTable(Table table, Person person) {
        if(!table.assignedSeats.isEmpty()) {
            table.assignedSeats.remove(person);

            //If still not empty, set the person positions
            if(!table.assignedSeats.isEmpty()) {
                setPersonPositions(table, new Vector3(table.bounds.x, table.bounds.y, 0));
            }
        }
    }

    public void addTableAtPosition(Vector3 pos) {
        //Add table to the closest mark
        Vector2 closestXY = null;
        double closestDistance = 0;
        for(Vector2 key : conference.snapGrid.keySet()) {
            double distance = Math.hypot(pos.x - key.x, pos.y - key.y);
            if(distance == 0 || closestDistance == 0 || distance < closestDistance) {
                if(conference.snapGrid.get(key) == null) {
                    closestDistance = distance;
                    closestXY = key;
                }
            }
        }
        if(closestXY != null) {
            Table table = new Table(closestXY);
            conference.snapGrid.put(closestXY, table);

            conference.getTables().add(table);
            table.tableIdentifier = String.valueOf(conference.getTables().size());
        }
    }

    public void removeTable(Table table) {
        conference.snapGrid.put(table.position, null); //Clear the snap position
        conference.getTables().remove(table); //remove the table from the conference

        for(int i = 0; i<conference.getTables().size(); i++) {
            conference.getTables().get(i).tableIdentifier = String.valueOf(i+1);
        }
    }

    public void moveTableToPosition(Table table, Vector3 pos) {
        Tween.to(table, EntityAccessor.POSITION_XY, .2f).target(pos.x,pos.y)
                .start(TweenUtil.getTweenManager());

        //Re-position the people as well
        setPersonPositions(table, pos);
    }

    private void setPersonPositions(Table table, Vector3 pos) {
        double angleBetweenSeats = 360 / table.assignedSeats.size();

        double nextSeatAngle = 0;
        for (Person person : table.assignedSeats) {
            double nextSeatRadians = Math.toRadians(nextSeatAngle);
            float x = (float) (table.getRadius() * Math.cos(nextSeatRadians)) + pos.x;
            float y = (float) (table.getRadius() * Math.sin(nextSeatRadians)) + pos.y;

            //Delay the movement of people for effect
            Tween.to(person, EntityAccessor.POSITION_XY, .2f).target(x,y)
                    .delay(new Random().nextFloat() * (.3f - .05f) + .05f) //Delay .05 to .3 seconds
                    .start(TweenUtil.getTweenManager());

            nextSeatAngle += angleBetweenSeats;
        }
    }
}
