package com.kozu.easyseating.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.kozu.easyseating.object.Conference;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

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

        return load("lastSavedFile", prefs.getString("lastSavedFile"));
    }

    public static Conference load(String conferenceName, String conferenceFileLocation) throws Exception {
        FileHandle file = Gdx.files.absolute(conferenceFileLocation);

        ObjectInputStream ois = null;
        Conference conference = null;
        try {
            try {
                ois = new ObjectInputStream(file.read());
            } catch(EOFException eof) {
                //Do nothing
            }
            try {
                conference = (Conference)ois.readObject();
                State.load(conference);
            } catch(EOFException eof) {
                //Do nothing
            }
        } catch(Throwable rte) {
            Preferences prefs = Gdx.app.getPreferences("SeatingChart");
            prefs.remove(conferenceName);
            prefs.flush();
            file.delete();
            throw new Exception("Unable to parse conference file.  Deleting prefs.", rte);
        } finally {
            if(ois != null)
                ois.close();
        }

        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        if(!prefs.contains("lastSavedFile")) {
            prefs.putString("lastSavedFile", file.path());
            prefs.flush();
        }

        return conference;
    }

    public static ArrayList<String> getListOfVenues() {
        ArrayList<String> listOfVenues = new ArrayList<String>();

        Preferences prefs = Gdx.app.getPreferences("SeatingChart");

        Map<String, ?> map = prefs.get();

        listOfVenues.addAll(map.keySet());

        listOfVenues.remove("lastSavedFile");

        return listOfVenues;
    }

    public static Conference loadConference(String conferenceName) throws Exception {
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");

        return load(conferenceName, prefs.getString(conferenceName));
    }

    public static void save() {
        //System.out.println("Saving");

        String fileSystemNameOfConference = currentConference.conferenceName.toLowerCase();

        FileHandle file = Gdx.files.absolute(Gdx.files.getLocalStoragePath()+"\\EasySeatingSaves\\"+fileSystemNameOfConference+".txt");

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(file.write(false));
            oos.writeObject(currentConference);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Set the last saved file in a preference file
        //%UserProfile%/.prefs/My Preferences
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");
        prefs.putString("lastSavedFile", file.path());
        prefs.putString(currentConference.conferenceName, file.path());
        prefs.flush();
    }

    /**
     * Deletes a loaded conference
     */
    public static void delete() {
        delete(currentConference.conferenceName);
    }

    /**
     * Deletes a conference saved file and removes the entry from the pref file
     */
    public static void delete(String conferenceName) {
        Preferences prefs = Gdx.app.getPreferences("SeatingChart");

        String conferenceFileLocation = prefs.getString(conferenceName.toLowerCase());

        FileHandle file = Gdx.files.absolute(conferenceFileLocation);
        //System.out.println("delete path:"+file.path());
        file.delete();

        if(conferenceFileLocation.equals(prefs.getString("lastSavedFile"))) {
            prefs.remove("lastSavedFile");
        }
        prefs.remove(conferenceName);

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

    public static boolean hasLoad() {
        ArrayList<String> venues = getListOfVenues();

        if(venues.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
