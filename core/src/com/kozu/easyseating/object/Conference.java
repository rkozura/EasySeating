package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rob on 8/3/2017.
 */

/**
 * A conference is where tables are setup at
 */
public class Conference implements Serializable {
    static final long serialVersionUID = 1L;

    public String conferenceName;
    public double conferenceWidth;
    public double conferenceHeight;
    public long gridCountWidth;
    public long gridCountHeight;
    public double gridGutterLength;

    public HashMap<Vector3, Table> snapGrid = new HashMap<Vector3, Table>();

    private ArrayList<Table> tables;
    public ArrayList<Person> persons;

    /**
     * Needed for deserialization
     */
    public Conference() {
        tables = new ArrayList<Table>();
        persons = new ArrayList<Person>();
    }

    public Conference(String conferenceName, double conferenceWidth, double conferenceHeight,
                      long gridCountWidth, long gridCountHeight, double gridGutterLength) {
        this();
        this.conferenceName = conferenceName;
        this.conferenceWidth = conferenceWidth;
        this.conferenceHeight = conferenceHeight;
        this.gridCountWidth = gridCountWidth;
        this.gridCountHeight = gridCountHeight;
        this.gridGutterLength = gridGutterLength;
    }

    public List<Table> getTables() {
        return tables;
    }
}
