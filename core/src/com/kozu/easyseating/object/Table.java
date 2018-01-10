package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table implements Model {
    private final int INIT_TABLE_RADIUS = 75;

    public String tableIdentifier;
    public Circle bounds;
    public Array<Person> assignedSeats;

    public Table() {}

    public Table(Vector3 position) {
        bounds = new Circle();
        bounds.set(position.x, position.y, INIT_TABLE_RADIUS);
        assignedSeats = new Array<>();
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

    public void setRadius(float radius) {
        bounds.radius = radius;
    }

    public float getRadius() {
        return bounds.radius;
    }
}