package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table implements Model {
    private final int INIT_TABLE_RADIUS = 75;

    public String tableIdentifier;
    public Circle bounds;
    public List<Person> assignedSeats;
    public Vector3 position;

    public Table() {}

    public Table(Vector3 position) {
        this.position = position;
        bounds = new Circle();
        bounds.set(position.x, position.y, INIT_TABLE_RADIUS);
        assignedSeats = new ArrayList<>();
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