package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;

import java.io.Serializable;

/**
 * Created by Rob on 8/2/2017.
 */

public class Person implements Model, Serializable {
    static final long serialVersionUID = 1L;

    private final int PERSON_RADIUS = 30;

    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private String initials = "";
    private String truncatedName = ""; //Used when display close up

    public Circle bounds;

    private transient boolean flaggedForRemoval;

    public String assignedTable;

    public Person() {}

    public Person(String firstName, String lastName) {
        setName(firstName, lastName);

        bounds = new Circle(0, 0, PERSON_RADIUS);
    }

    public void setName(String firstName, String lastName) {
        if(firstName.length() > 1) {
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        } else if (firstName.length() == 1) {
            firstName = firstName.substring(0, 1).toUpperCase();
        } else {
            firstName = "";
        }

        if(lastName.length() > 1) {
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        } else if (lastName.length() == 1) {
            lastName = lastName.substring(0, 1).toUpperCase();
        } else {
            lastName = "";
        }

        String firstInitial = "";
        String lastInitial = "";
        if(!firstName.isEmpty()) {
            firstInitial = ""+firstName.charAt(0);
        }
        if(!lastName.isEmpty()) {
            lastInitial = ""+lastName.charAt(0);
        }
        initials = firstInitial+lastInitial;

        if(!lastName.isEmpty()) {
            truncatedName = lastName + "." + firstInitial;
        } else {
            truncatedName = firstName;
        }

        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Person && firstName.equals(((Person)o).getFirstName()) && lastName.equals(((Person)o).getLastName())) {
            return true;
        } else {
            return false;
        }
    }

    public String getInitials() {
        return initials;
    }

    public String getTruncatedName() {
        return truncatedName;
    }

    public boolean isFlaggedForRemoval() {
        return flaggedForRemoval;
    }

    public void setFlaggedForRemoval(boolean flaggedForRemoval) {
        this.flaggedForRemoval = flaggedForRemoval;
    }
}
