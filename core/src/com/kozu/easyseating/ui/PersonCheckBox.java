package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

/**
 * Created by Rob on 8/6/2017.
 */

public class PersonCheckBox extends CheckBox {
    public Person person;
    public Actor defaultImageActor;

    public PersonCheckBox(final SeatingLogic seatingLogic, final Person person, Skin skin) {
        super(person.getName(), skin);
        this.person = person;
        this.getImageCell().padLeft(10).padRight(10);
        this.getLabelCell().width(1000);
        defaultImageActor = this.getImageCell().getActor();
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(PersonCheckBox.this.isChecked()) {
                    seatingLogic.addPersonToTable(seatingLogic.tappedTable, person);
                    PersonCheckBox.this.getImageCell().setActor(PersonCheckBox.this.defaultImageActor);
                } else {
                    seatingLogic.removePersonFromTable(seatingLogic.tappedTable, person);
                }
            }
        });
    }
}
