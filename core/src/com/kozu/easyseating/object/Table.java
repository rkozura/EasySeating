package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table implements Entity {
    private int TABLE_RADIUS = 75;

    public Circle bounds;
    public List<Person> assignedSeats;

    public Table(float x, float y) {
        bounds = new Circle();
        bounds.set(x, y, TABLE_RADIUS);
        assignedSeats = new ArrayList<Person>();
    }

    @Override
    public float getX() {
        return bounds.x;
    }

    @Override
    public float getY() {
        return bounds.y;
    }

    @Override
    public void setX(float x) {
        bounds.x = x;
    }

    @Override
    public void setY(float y) {
        bounds.y = y;
    }
}