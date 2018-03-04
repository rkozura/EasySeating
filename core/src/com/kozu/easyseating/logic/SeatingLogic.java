package com.kozu.easyseating.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final double CONFERENCE_WIDTH = 1600f;
    private final long GRID_COUNT_WIDTH = 20;
    private final long GRID_COUNT_HEIGHT = Math.round(GRID_COUNT_WIDTH*1.5);
    private final double GRID_GUTTER_LENGTH = CONFERENCE_WIDTH/GRID_COUNT_WIDTH;
    private final double CONFERENCE_HEIGHT = GRID_GUTTER_LENGTH*GRID_COUNT_HEIGHT;;

    public Conference conference;

    private Color backgroundTint = new Color(1, 1, 1, 1);

    public Color getBackgroundTint() {
        return backgroundTint;
    }

    public void setBackgroundTint(Color backgroundTint) {
        this.backgroundTint = backgroundTint;
    }

    public SeatingLogic(Conference conference) {
        this.conference = conference;
        init();
    }

    private void init() {
        //Load the conference object into the state
        State.load(conference);
        State.save();

        //Generate the snap grid if empty
        if(conference.snapGrid.isEmpty()) {
            double currentX = GRID_GUTTER_LENGTH, currentY = GRID_GUTTER_LENGTH;
            for (int i = 0; i < GRID_COUNT_WIDTH - 1; i++) {
                for (int j = 0; j < GRID_COUNT_HEIGHT - 1; j++) {
                    conference.snapGrid.put(new Vector3((float) currentX, (float) currentY, 0), null);
                    currentY += GRID_GUTTER_LENGTH;
                }
                currentY = GRID_GUTTER_LENGTH;
                currentX += GRID_GUTTER_LENGTH;
            }
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

    public Person getPersonAtPosition(Vector3 pos, Table table) {
        Person returnPerson = null;

        for(Person person : table.assignedSeats) {
            if(person.bounds.contains(pos.x, pos.y)) {
                returnPerson = person;
                break;
            }
        }

        return returnPerson;
    }

    public Person createPerson(String firstName, String lastName) {
        Person person = new Person(firstName, lastName);
        conference.persons.add(person);

        return person;
    }

    public void removePerson(Person person) {
        conference.persons.remove(person);
        for(Table table : conference.getTables()) {
            if(table.assignedSeats.contains(person)) {
                table.removePerson(person);
                break;
            }
        }
    }

    public Timeline addPersonToTable(Table table, Person person) {
        //Find the person in an existing table and remove
        for(Table allTable : conference.getTables()) {
            if(allTable.assignedSeats.contains(person)) {
                removePersonFromTable(allTable, person);
                break;
            }
        }

        person.assignedTable = table.tableIdentifier;
        table.addPerson(person);
        return startTweenAndSaveState(getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0)));
    }

    public void removePersonFromTable(Table table, Person person) {
        person.setFlaggedForRemoval(false);
        person.assignedTable = "";
        if(table.assignedSeats.size() != 0) {
            table.removePerson(person);

            //If not empty, set the person positions
            if(table.assignedSeats.size() != 0) {
                startTweenAndSaveState(getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0)));
            }
        }
    }

    private Timeline startTweenAndSaveState(List<Tween> tweens) {
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

        return timeline;
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
        for(Person person : table.assignedSeats) {
            person.assignedTable = "";
        }
        table.assignedSeats.clear();

        conference.getTables().remove(table); //remove the table from the conference

        for(int i = 0; i<conference.getTables().size(); i++) {
            conference.getTables().get(i).tableIdentifier = String.valueOf(i+1);
        }

        for(Table conferenceTable : conference.getTables()) {
            for(Person person : conferenceTable.assignedSeats) {
                person.assignedTable = conferenceTable.tableIdentifier;
            }
        }

        for(Map.Entry<Vector3, Table> entry : conference.snapGrid.entrySet()) {
            if(table == entry.getValue()) {
                entry.setValue(null);
                break;
            }
        }

        State.save();
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

        if(closestEntry != null && closestEntry.getValue() == null) {
            //Clear the snap position that contained the table
            for(Map.Entry<Vector3, Table> entry : conference.snapGrid.entrySet()) {
                if(entry.getValue() == table) {
                    entry.setValue(null);
                    break;
                }
            }

            closestEntry.setValue(table);
            setPersonPositions(table, closestEntry.getKey());
            table.setX(closestEntry.getKey().x);
            table.setY(closestEntry.getKey().y);
        }
    }

    public void resetTable(Table table) {
        List<Tween> tweens = getPersonPositionTweens(table, new Vector3(table.bounds.x, table.bounds.y, 0));
        startTweenAndSaveState(tweens);
    }

    private void setPersonPositions(Table table, Vector3 pos) {
        float personTableRadius = table.getRadius() + 40;
        List<Tween> returnList = new ArrayList<Tween>();

        if(table.assignedSeats.size() != 0) {
            double angleBetweenSeats = 360 / table.assignedSeats.size();

            double nextSeatAngle = 0;
            for (Person person : table.assignedSeats) {
                double nextSeatRadians = Math.toRadians(nextSeatAngle);
                float x = (float) (personTableRadius * Math.cos(nextSeatRadians)) + pos.x;
                float y = (float) (personTableRadius * Math.sin(nextSeatRadians)) + pos.y;

                person.setX(x);
                person.setY(y);

                nextSeatAngle += angleBetweenSeats;
            }
        }
    }

    private List<Tween> getPersonPositionTweens(Table table, Vector3 pos) {
        float personTableRadius = table.getRadius() + 40;
        List<Tween> returnList = new ArrayList<Tween>();

        if(table.assignedSeats.size() != 0) {
            double angleBetweenSeats = 360 / table.assignedSeats.size();

            double nextSeatAngle = 0;
            for (Person person : table.assignedSeats) {
                double nextSeatRadians = Math.toRadians(nextSeatAngle);
                float x = (float) (personTableRadius * Math.cos(nextSeatRadians)) + pos.x;
                float y = (float) (personTableRadius * Math.sin(nextSeatRadians)) + pos.y;

                returnList.add(Tween.to(person, EntityAccessor.POSITION_XY, .2f).target(x, y));

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

    private boolean movingTable;
    public boolean isMovingTable() {
        return movingTable;
    }

    public void setMovingTable(boolean isMovingTable){
        movingTable = isMovingTable;
    }
}
