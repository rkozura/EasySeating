package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.isAlphaSpace;

/**
 * Created by Rob on 01/3/2018.
 */

public class RenameVenueView extends AbstractLmlInputDialogView {
    private ToastManager toastManager;

    private SeatingLogic seatingLogic;

    @LmlActor("renameVenueDialog") private DialogSize renameVenueDialog;

    @LmlActor("venueName") private VisTextField venueName;

    public RenameVenueView(Stage stage, SeatingLogic seatingLogic) {
        super(stage);
        this.seatingLogic = seatingLogic;
    }

    @Override
    public String getViewId() {
        return "101";
    }

    private String initialText;
    public void setTextFieldString(String value) {
        initialText = value;
        venueName.setText(value);

        //The following three lines are absolutely needed for correct input on mobile
        FocusManager.switchFocus(getStage(), venueName);
        getStage().setKeyboardFocus(venueName);
        Gdx.input.setOnscreenKeyboardVisible(true);
        venueName.setCursorAtTextEnd();
    }

    @LmlAction("checkForInvalidVenueName")
    public boolean checkForInvalidVenueName(final DialogSize dialog) {
        String venueNameString = venueName.getText();

        ArrayList<String> venues = State.getListOfVenues();
        ArrayList<String> venuesFormatted = new ArrayList<String>();
        for(String venue: venues) {
            venuesFormatted.add(venue.toLowerCase());
        }

        if(StringUtils.isBlank(venueNameString)) {
            final ToastManager manager = getToastManager(dialog.getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Invalid Venue Name", 1.5f);
            manager.toFront();

            //The following three lines are absolutely needed for correct input on mobile
            FocusManager.switchFocus(getStage(), venueName);
            getStage().setKeyboardFocus(venueName);
            Gdx.input.setOnscreenKeyboardVisible(true);
            venueName.setCursorAtTextEnd();

            return ReflectedLmlDialog.CANCEL_HIDING;

        } else if(!initialText.equalsIgnoreCase(venueNameString) && venuesFormatted.contains(venueNameString.toLowerCase())) {
            final ToastManager manager = getToastManager(dialog.getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Venue name already exists", 1.5f);
            manager.toFront();

            //The following three lines are absolutely needed for correct input on mobile
            FocusManager.switchFocus(getStage(), venueName);
            getStage().setKeyboardFocus(venueName);
            Gdx.input.setOnscreenKeyboardVisible(true);
            venueName.setCursorAtTextEnd();

            return ReflectedLmlDialog.CANCEL_HIDING;
        } else {
            State.rename(venueNameString);

            return ReflectedLmlDialog.HIDE;
        }
    }

    @LmlAction("initTextField")
    public void initTextField(final VisTextField visTextField) {
        visTextField.setTextFieldFilter(new VisTextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(VisTextField textField, char c) {
                if(isAlphaSpace((Character.toString(c)))) {
                    if(textField.getText().length() >= 16) {
                        ToastManager manager = getToastManager(getStage());
                        manager.clear();
                        manager.setAlignment(Align.topLeft);
                        manager.show("Name too long!", 1.5f);
                        manager.toFront();

                        return false;
                    } else {
                        return true;
                    }
                } else {
                    ToastManager manager = getToastManager(getStage());
                    manager.clear();
                    manager.setAlignment(Align.topLeft);
                    manager.show("Invalid character!", 1.5f);
                    manager.toFront();

                    return false;
                }
            }
        });
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }

    @Override
    public DialogSize getDialog() {
        return renameVenueDialog;
    }
}
