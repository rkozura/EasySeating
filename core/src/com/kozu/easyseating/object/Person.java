package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Rob on 8/2/2017.
 */

public class Person implements Model {
    private String name;
    private String initials = "";

    public Vector2 position = new Vector2();

    public Person() {}

    public Person(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        initials = "";
        String[] names = name.split(" ");
        for(String oneName : names) {
            initials += oneName.charAt(0);
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
}
