package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Rob on 8/2/2017.
 */

public class Person implements Entity {
    private String name;
    public Vector2 position = new Vector2();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
