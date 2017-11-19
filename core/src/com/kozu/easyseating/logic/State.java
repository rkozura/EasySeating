package com.kozu.easyseating.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.kozu.easyseating.object.Conference;

/**
 * Created by Rob on 11/15/2017.
 */

public class State {
    private static Conference currentConference;

    public static void load(Conference conference) {
        currentConference = conference;
    }

    public static void save() {
        System.out.println("Saving");

        Json json = new Json();
        json.setUsePrototypes(false);

        String fileSystemNameOfConference = currentConference.conferenceName.toLowerCase();
        //TODO Make the file location happy with all devices
        //C:\Users\Rob\AndroidStudioProjects\EasySeating\android\assets\EasySeatingSaves
        FileHandle file = Gdx.files.absolute(Gdx.files.getLocalStoragePath()+"\\EasySeatingSaves\\"+fileSystemNameOfConference+".txt");

        file.writeString(json.toJson(currentConference), false);

        //Set the last saved file in a preference file
        //%UserProfile%/.prefs/My Preferences
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        prefs.putString("lastSavedFile", file.path());
        prefs.putString(currentConference.conferenceName, fileSystemNameOfConference);
        prefs.flush();
    }
}
