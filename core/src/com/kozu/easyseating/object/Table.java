package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table implements Model, Serializable {
    private final int DISTANCE_BETWEEN_PEOPLE = 60;
    private final int INIT_TABLE_RADIUS = 75;

    public String tableIdentifier;
    public Circle bounds;
    public ArrayList<Person> assignedSeats;

    public Table() {}

    public Table(Vector3 position) {
        bounds = new Circle();
        bounds.set(position.x, position.y, INIT_TABLE_RADIUS);
        assignedSeats = new ArrayList<Person>();

        calculateRadius();
    }

    public void addPerson(Person person) {
        assignedSeats.add(person);

        calculateRadius();
    }

    public void removePerson(Person person) {
        assignedSeats.remove(person);

        calculateRadius();
    }

    public void calculateRadius() {
        int tableSize = assignedSeats.size();
        if(assignedSeats.size() < 8) {
            tableSize = 8;
        }

        int circumference = DISTANCE_BETWEEN_PEOPLE * tableSize;
        bounds.setRadius(circumference / (2* MathUtils.PI));
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
