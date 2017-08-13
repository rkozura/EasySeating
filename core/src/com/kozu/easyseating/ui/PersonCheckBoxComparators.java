package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Comparator;

/**
 * Created by Rob on 8/7/2017.
 */

public abstract class PersonCheckBoxComparators {

    static class PersonCheckBoxNameComparator implements Comparator<Actor> {
        @Override
        public int compare(Actor o, Actor t1) {
            PersonCheckBox firstArg = (PersonCheckBox)o;
            PersonCheckBox secondArg = (PersonCheckBox)t1;

            int returnValue = firstArg.person.getName().compareTo(secondArg.person.getName());

            if(firstArg.isChecked() && !secondArg.isChecked()) {
                returnValue = -1;
            } else if(secondArg.isChecked() && !firstArg.isChecked()) {
                returnValue = 1;
            }

            return returnValue;
        }
    }

}
