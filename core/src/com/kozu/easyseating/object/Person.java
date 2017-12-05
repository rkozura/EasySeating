package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Rob on 8/2/2017.
 */

public class Person implements Model {
    private final int PERSON_RADIUS = 30;

    private String name;
    private String initials = "";
    private String truncatedName = ""; //Used when display close up

    public Circle bounds;
    public Vector2 position = new Vector2();

    public Person() {}

    public Person(String name) {
        setName(name);

        bounds = new Circle();
        bounds.set(position.x, position.y, PERSON_RADIUS);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.trim();
        name = name.replaceAll("[ ]+", " ");
        this.name = name;

        initials = "";
        String[] names = name.split(" ");
        for(String oneName : names) {
            initials += oneName.charAt(0);
        }

        truncatedName = names[0];
        if(names.length > 1) {
            truncatedName += "." + names[1].charAt(0);
        }
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public void setX(float x) {
        position.x = x;
    }

    @Override
    public void setY(float y) {
        position.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Person && name.equals(((Person)o).getName())) {
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
}
