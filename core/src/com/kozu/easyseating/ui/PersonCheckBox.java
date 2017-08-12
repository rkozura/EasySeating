package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kozu.easyseating.object.Person;

/**
 * Created by Rob on 8/6/2017.
 */

public class PersonCheckBox extends CheckBox {
    public Person person;

    public PersonCheckBox(String name, Skin skin) {
        super(name, skin);
    }
}
