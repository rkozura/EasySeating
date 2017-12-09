package com.kozu.easyseating.logic;

import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * SeatingLogic contains all the operations one can do to the conference.  Also contains selected
 * and tapped tables for use by the controller to determine what happens to gestures
 *
 * Created by Rob on 8/2/2017.
 */
public class SeatingLogic {
    //1.5 is approx. the ratio of a piece of paper
    private final double CONFERENCE_WIDTH = 1000f;
    private final long GRID_COUNT_WIDTH = 15;
    private final long GRID_COUNT_HEIGHT = Math.round(GRID_COUNT_WIDTH*1.5);
    private final double GRID_GUTTER_LENGTH = CONFERENCE_WIDTH/GRID_COUNT_WIDTH;
    private final double CONFERENCE_HEIGHT = GRID_GUTTER_LENGTH*GRID_COUNT_HEIGHT;;

    public Conference conference;

    public SeatingLogic(Conference conference) {
        this.conference = conference;
        init();
    }

    private void init() {
        //Load the conference object into the state
        State.load(conference);

        //Generate the snap grid
        double currentX = GRID_GUTTER_LENGTH, currentY = GRID_GUTTER_LENGTH;
        for(int i=0; i<GRID_COUNT_WIDTH-1; i++) {
            for(int j=0; j<GRID_COUNT_HEIGHT-1; j++) {
                conference.snapGrid.put(new Vector3((float)currentX, (float)currentY, 0), null);
                currentY += GRID_GUTTER_LENGTH;
            }
            currentY = GRID_GUTTER_LENGTH;
            currentX += GRID_GUTTER_LENGTH;
        }
    }

    public SeatingLogic(String conferenceName) {
        conference = new Conference(conferenceName, CONFERENCE_WIDTH, CONFERENCE_HEIGHT,
                GRID_COUNT_WIDTH, GRID_COUNT_HEIGHT, GRID_GUTTER_LENGTH);
        init();
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
        startTweenAndSaveState(getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0)));
    }

    public void removePersonFromTable(Table table, Person person) {
        if(!table.assignedSeats.isEmpty()) {
            table.assignedSeats.remove(person);

            //If still not empty, set the person positions
            if(!table.assignedSeats.isEmpty()) {
                startTweenAndSaveState(getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0)));
            }
        }
    }

    private void startTweenAndSaveState(List<Tween> tweens) {
        Timeline timeline = Timeline.createParallel();

        //Move the people as well
        for(Tween tween : tweens) {
            timeline.push(tween);
        }

        timeline.setCallbackTriggers(TweenCallback.COMPLETE);
        timeline.setCallback(new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                State.save();
            }
        });
        timeline.start(TweenUtil.getTweenManager());
    }

    public void addTableAtPosition(Vector3 pos) {
        //Add table to the closest mark
        Vector3 closestXY = null;
        double closestDistance = 0;
        for(Vector3 key : conference.snapGrid.keySet()) {
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

        State.save();
    }

    public void removeTable(Table table) {
        //TODO rethink the snap grid...do I really need to store the table in the map?
        //conference.snapGrid.put(new Vector3(table.bounds.x, table.bounds.y, 0), null); //Clear the snap position
        conference.getTables().remove(table); //remove the table from the conference

        for(int i = 0; i<conference.getTables().size(); i++) {
            conference.getTables().get(i).tableIdentifier = String.valueOf(i+1);
        }
    }

    public boolean isPersonAtTable(Table table, Person person) {
        return table.assignedSeats.contains(person);
    }

    public void moveTableToPosition(Table table, Vector3 pos) {
        float closestDistance = -1;
        Map.Entry<Vector3, Table> closestEntry = null;
        //Find a snap location for the table
        for(Map.Entry<Vector3, Table> entry : conference.snapGrid.entrySet()) {
            Vector3 point = entry.getKey();
            float distance = point.dst(pos);
            if(closestDistance == -1 || distance < closestDistance) {
                closestDistance = distance;
                closestEntry = entry;
            }
        }

        if(closestEntry != null) {
            closestEntry.setValue(table);
            List<Tween> tweens = getPersonPositionTweens(table, closestEntry.getKey());
            tweens.add(Tween.to(table, EntityAccessor.POSITION_XY, .2f).target(closestEntry.getKey().x, closestEntry.getKey().y));

            startTweenAndSaveState(tweens);
        }
    }

    public void resetTable(Table table) {
        List<Tween> tweens = getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0));
        startTweenAndSaveState(tweens);
    }

    private List<Tween> getPersonPositionTweens(Table table, Vector3 pos) {
        List<Tween> returnList = new ArrayList<Tween>();

        if(table.assignedSeats.size() != 0) {
            double angleBetweenSeats = 360 / table.assignedSeats.size();

            double nextSeatAngle = 0;
            for (Person person : table.assignedSeats) {
                double nextSeatRadians = Math.toRadians(nextSeatAngle);
                float x = (float) (table.getRadius() * Math.cos(nextSeatRadians)) + pos.x;
                float y = (float) (table.getRadius() * Math.sin(nextSeatRadians)) + pos.y;

                //Delay the movement of people for effect
                returnList.add(Tween.to(person, EntityAccessor.POSITION_XY, .2f).target(x, y)
                        .delay(new Random().nextFloat() * (.3f - .05f) + .05f)); //Delay .05 to .3 sec

                nextSeatAngle += angleBetweenSeats;
            }
        }

        return returnList;
    }

    public Table getAssignedTable(Person person) {
        Table returnTable = null;

        for(Table table : conference.getTables()) {
            if(table.assignedSeats.contains(person)) {
                returnTable = table;
                break;
            }
        }

        return returnTable;
    }

    private boolean overTable;
    public Table table;
    public Person person;
    public void setOverTable(boolean b, Table table, Person person) {
        this.table = table;
        this.person = person;
        overTable = b;
    }

    public boolean isOverTable() {
        return overTable;
    }
}
