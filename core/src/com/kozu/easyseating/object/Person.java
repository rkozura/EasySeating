package com.kozu.easyseating.object;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Rob on 8/2/2017.
 */

public class Person {
    private String name;
    public Vector2 position = new Vector2();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
