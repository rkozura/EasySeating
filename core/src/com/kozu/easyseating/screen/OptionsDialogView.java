package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;

/**
 * Created by Rob on 11/22/2017.
 */

public class OptionsDialogView extends AbstractLmlView {
    private SeatingLogic seatingLogic;


    public OptionsDialogView(Stage stage, SeatingLogic seatingLogic) {
        super(stage);
        this.seatingLogic = seatingLogic;
    }

    @Override
    public String getViewId() {
        return "100";
    }

    @LmlAction("navigateToMainMenu")
    public void navigateToMainMenu() {
        ((EasySeatingGame) Gdx.app.getApplicationListener()).navigateToMainMenu();
    }

    @LmlAction("openRenameVenueDialog")
    public void openRenameVenueDialog() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        RenameVenueView renameVenueView = new RenameVenueView(getStage(), seatingLogic);
        core.getParser().createView(renameVenueView, Gdx.files.internal("views/RenameVenueView.lml"));
        renameVenueView.setTextFieldString(seatingLogic.conference.conferenceName);
        renameVenueView.moveToTop(renameVenueView.getDialog());
    }

    @LmlAction("openHelpDialog")
    public void openHelpDialog() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        HelpDialogView renameVenueView = new HelpDialogView(getStage());
        core.getParser().createView(renameVenueView, renameVenueView.getTemplateFile());
    }

    @LmlAction("export")
    public void export() {
        EasySeatingGame.pdfGenerator.generatePDF(seatingLogic.conference);
    }
}
