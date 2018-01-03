package com.kozu.easyseating.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.kozu.easyseating.object.Conference;

/**
 * Created by Rob on 11/15/2017.
 */

public class State {
    private static Conference currentConference;

    public static void load(Conference conference) {
        currentConference = conference;
    }

    public static Conference loadLast() throws Exception {
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        FileHandle file = Gdx.files.absolute(prefs.getString("lastSavedFile"));

        Json json = new Json();
        json.setUsePrototypes(false);

        Conference conference;
        try {
            conference = json.fromJson(Conference.class, file);
            State.load(conference);
        } catch(SerializationException rte) {
            prefs.clear();
            file.delete();
            throw new Exception("Unable to parse conference file.  Deleting prefs.", rte);
        }

        return conference;
    }

    public static void save() {
        System.out.println("Saving");

        Json json = new Json();
        json.setUsePrototypes(false);

        String fileSystemNameOfConference = currentConference.conferenceName.toLowerCase();

        FileHandle file = Gdx.files.absolute(Gdx.files.getLocalStoragePath()+"\\EasySeatingSaves\\"+fileSystemNameOfConference+".txt");

        file.writeString(json.toJson(currentConference), false);

        //Set the last saved file in a preference file
        //%UserProfile%/.prefs/My Preferences
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        prefs.putString("lastSavedFile", file.path());
        prefs.putString(currentConference.conferenceName, fileSystemNameOfConference);
        prefs.flush();
    }

    public static void delete() {
        String fileSystemNameOfConference = currentConference.conferenceName.toLowerCase();
        FileHandle file = Gdx.files.absolute(Gdx.files.getLocalStoragePath()+"\\EasySeatingSaves\\"+fileSystemNameOfConference+".txt");
        file.delete();

        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        prefs.remove("lastSavedFile");
        prefs.remove(currentConference.conferenceName);
        prefs.flush();
    }

    public static void rename(String name) {
        delete();
        currentConference.conferenceName = name;
        save();
    }

    public static boolean hasContinue() {
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        if(prefs.contains("lastSavedFile")) {
            FileHandle file = Gdx.files.absolute(prefs.getString("lastSavedFile"));
            if(!file.exists()) {
                //Delete entry in preferences file if file does not exist
                prefs.clear();
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
