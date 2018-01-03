package com.kozu.easyseating.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Rob on 01/3/2018.
 */

public class RenameVenueView extends AbstractLmlView {
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

    public void setTextFieldString(String value) {
        venueName.setText(value);
    }

    @LmlAction("checkForInvalidVenueName")
    public boolean checkForInvalidVenueName(final DialogSize dialog) {
        String venueNameString = venueName.getText();
        if(!StringUtils.isBlank(venueNameString)) {
            seatingLogic.conference.conferenceName = venueNameString;
            State.rename(seatingLogic.conference);

            return ReflectedLmlDialog.HIDE;
        } else {
            return ReflectedLmlDialog.CANCEL_HIDING;
        }
    }
}
