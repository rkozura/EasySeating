package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
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
            SeatingScreen seatingView = core.getParser().createView(SeatingScreen.class, Gdx.files.internal("views/SeatingView.lml"));
            seatingView.setConferenceName(venueName);
            core.setView(seatingView);

            return ReflectedLmlDialog.HIDE;
        }
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }

    //TODO Unable to focus keyboard on adding to stage
    @LmlAction("setup")
    public void setup(final VisTextField textField) {
        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if (!focused) Gdx.input.setOnscreenKeyboardVisible(false);
            }
        });
    }
}
