package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
    @LmlActor("createVenueDialog") private DialogSize createVenueDialog;

    @LmlActor("venueName") private VisTextField venueName;

    private ToastManager toastManager;

    public MainScreen() {
        super(new Stage());
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/MainMenuView.lml");
    }

    @Override
    public String getViewId() {
        return "second";
    }

    @LmlAction("checkForInvalidVenueName")
    public boolean checkForInvalidVenueName(final DialogSize dialog) {
        String venueName = ((VisTextField) LmlUtilities.getActorWithId(dialog, "venueName")).getText();
        if(StringUtils.isBlank(venueName)) {
            final ToastManager manager = getToastManager(dialog.getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Invalid Venue Name", 1.5f);
            manager.toFront();

            return ReflectedLmlDialog.CANCEL_HIDING;

        } else {
            EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
            SeatingScreen seatingScreen = new SeatingScreen();
            seatingScreen.setConferenceName(venueName);

            Array<Actor> seatingView = core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());

            core.setView(seatingScreen);
            LmlUtilities.appendActorsToStage(seatingScreen.getStage(), seatingView);

            return ReflectedLmlDialog.HIDE;
        }
    }

    @LmlAction("openCreateVenueDialog")
    public void openCreateVenueDialog() {
        createVenueDialog.setVisible(true);
        createVenueDialog.show(getStage());
        createVenueDialog.toFront();

        createVenueDialog.setPosition(createVenueDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        getStage().setKeyboardFocus(venueName);
        Gdx.input.setOnscreenKeyboardVisible(true);
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }

    @LmlAction("ppi")
    public float getPPI(final VisTable container) {
        return Gdx.graphics.getPpiX();
    }
}
