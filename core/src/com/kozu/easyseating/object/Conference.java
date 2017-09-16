package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 8/3/2017.
 */

/**
 * A conference is where tables are setup at
 */
public class Conference {
    public String conferenceName;
    public double conferenceWidth;
    public double conferenceHeight;
    public long gridCountWidth;
    public long gridCountHeight;
    public double gridGutterLength;

    public Map<Vector2, Table> snapGrid;

    private List<Table> tables;
    public Array<Person> persons;

    public Conference(String conferenceName, double conferenceWidth, double conferenceHeight,
                      long gridCountWidth, long gridCountHeight, double gridGutterLength) {
        this.conferenceName = conferenceName;
        this.tables = new ArrayList<Table>();
        //this.persons = new Array<Person>();
        this.conferenceWidth = conferenceWidth;
        this.conferenceHeight = conferenceHeight;
        this.gridCountWidth = gridCountWidth;
        this.gridCountHeight = gridCountHeight;
        this.gridGutterLength = gridGutterLength;

        snapGrid = new HashMap<>();
        persons = new Array<Person>();
    }

    public List<Table> getTables() {
        return tables;
    }
}
