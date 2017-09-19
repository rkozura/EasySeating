package com.kozu.easyseating.resolver;

import com.badlogic.gdx.utils.Array;
import com.kozu.easyseating.object.Person;

/**
 * Created by Rob on 8/31/2017.
 */

public interface PersonImporter {
    Array<Person> getPersonList();
}
