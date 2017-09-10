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
    public float conferenceWidth;
    public float conferenceHeight;
    public Map<Vector2, Table> snapGrid;
    private List<Table> tables;
    public static Array<Person> persons = new Array<Person>();

    public Conference(String conferenceName, float conferenceWidth, float conferenceHeight) {
        this.conferenceName = conferenceName;
        this.tables = new ArrayList<Table>();
        //this.persons = new Array<Person>();
        this.conferenceWidth = conferenceWidth;
        this.conferenceHeight = conferenceHeight;

        snapGrid = new HashMap<>();
    }

    public List<Table> getTables() {
        return tables;
    }
}
