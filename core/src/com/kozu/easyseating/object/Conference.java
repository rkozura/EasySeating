package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector2;

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
    public float conferenceWidth;
    public float conferenceHeight;
    public Map<Vector2, Table> snapGrid;
    private List<Table> tables;
    public List<Person> persons;

    public Conference(float conferenceWidth, float conferenceHeight) {
        this.tables = new ArrayList<Table>();
        this.persons = new ArrayList<Person>();
        this.conferenceWidth = conferenceWidth;
        this.conferenceHeight = conferenceHeight;

        snapGrid = new HashMap<>();
    }

    public List<Table> getTables() {
        return tables;
    }
}
