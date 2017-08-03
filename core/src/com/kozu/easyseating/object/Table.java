package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table {
    private int TABLE_RADIUS = 75;

    public Circle bounds;
    public List<Person> assignedSeats;

    public Table(float x, float y) {
        bounds = new Circle();
        bounds.set(x, y, TABLE_RADIUS);

        assignedSeats = new ArrayList<Person>();
        assignedSeats.add(new Person());
        assignedSeats.add(new Person());
    }
}