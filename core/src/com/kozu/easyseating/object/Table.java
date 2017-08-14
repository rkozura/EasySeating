package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table implements Model {
    private int TABLE_RADIUS = 75;

    public String tableIdentifier;
    public Circle bounds;
    public List<Person> assignedSeats;
    public Vector2 position;

    public Table(Vector2 position) {
        this.position = position;
        bounds = new Circle();
        bounds.set(position.x, position.y, TABLE_RADIUS);
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
}