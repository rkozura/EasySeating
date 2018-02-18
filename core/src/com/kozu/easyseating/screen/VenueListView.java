package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.ui.PeopleListAdapter;
import com.kozu.easyseating.ui.VenueListAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Rob on 02/18/2018
 */

public class VenueListView extends AbstractLmlView {
    private VenueListAdapter venueListAdapter;
    private Assets assets;

    public VenueListView(Stage stage, Assets assets) {
        super(stage);
        this.assets = assets;
    }

    @Override
    public String getViewId() {
        return "109";
    }

    private String selectedVenue;
    @LmlAction("loadSelectedVenue")
    public void loadSelectedVenue() {
        try {
            Conference conference = State.loadConference(selectedVenue);

            State.load(conference);

            EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();

            try {
                SeatingScreen seatingScreen = new SeatingScreen(conference, assets);

                core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());
                core.setView(seatingScreen);
            } catch(Exception e) {
                venueListAdapter.itemsChanged();
                venueListAdapter.itemsDataChanged();
                //TODO Show confirmation message that the data is corrupt and want to remove
                e.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @LmlAction("venueListAdapter")
    public ListAdapter<?> venueListAdapter() {
        venueListAdapter = new VenueListAdapter<String>(State.getListOfVenues());
        return venueListAdapter;
    }

    @LmlAction("venueListListener")
    public void venueListListener(final String selectedItem) {
        selectedVenue = selectedItem;
        System.out.println("selected: "+selectedItem);


    }

    @LmlAction("dialogHeight")
    public float getDialogHeight(final VisTable container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX()*1.2f;
    }
}
