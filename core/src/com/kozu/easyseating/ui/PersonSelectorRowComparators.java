package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Comparator;

/**
 * Created by Rob on 8/18/2017.
 */

public class PersonSelectorRowComparators {
    static class PersonSelectorRowNameComparator implements Comparator<Actor> {
        @Override
        public int compare(Actor o, Actor t1) {
            PersonSelectorRow firstArg = (PersonSelectorRow)o;
            PersonSelectorRow secondArg = (PersonSelectorRow)t1;

            int returnValue = firstArg.person.getName().compareTo(secondArg.person.getName());

            if(firstArg.checked && !secondArg.checked) {
                returnValue = -1;
            } else if(secondArg.checked && !firstArg.checked) {
                returnValue = 1;
            }

            return returnValue;
        }
    }
}