package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.ui.DialogSize;
import com.kozu.easyseating.ui.VenueListAdapter;

/**
 * Created by Rob on 02/18/2018
 */

public class VenueListView extends AbstractLmlView {
    @LmlActor("venueListDialog") private DialogSize venueListDialog;

    private VenueListAdapter<String> venueListAdapter;
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
        if(selectedVenue != null) {
            try {
                Conference conference = State.loadConference(selectedVenue);

                State.load(conference);

                EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();

                try {
                    SeatingScreen seatingScreen = new SeatingScreen(conference, assets);

                    core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());
                    core.setView(seatingScreen);
                } catch (Exception e) {
                    venueListAdapter.itemsChanged();
                    venueListAdapter.itemsDataChanged();
                    //TODO Show confirmation message that the data is corrupt and want to remove
                    e.printStackTrace();
                } finally {
                    venueListDialog.hide();
                    selectedVenue = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @LmlAction("deleteSelectedVenue")
    public void deleteSelectedVenue() {
        if(selectedVenue != null) {
            State.delete(selectedVenue);
            venueListAdapter.remove(selectedVenue);
            venueListAdapter.itemsChanged();
            selectedVenue = null;
        }
    }

    @LmlAction("venueListAdapter")
    public ListAdapter<?> venueListAdapter() {
        venueListAdapter = new VenueListAdapter<String>(State.getListOfVenues());
        return venueListAdapter;
    }

    @LmlAction("venueListListener")
    public void venueListListener(final String selectedItem) {
        // Clear the existing selection
        venueListAdapter.itemsChanged();

        VisTable view = venueListAdapter.getView(selectedItem);

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(new Color(0, .502f, .949f, 1));
        pm1.fill();

        view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

        selectedVenue = selectedItem;
    }

    @LmlAction("dialogHeight")
    public float getDialogHeight(final VisTable container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX()*1.2f;
    }
}
