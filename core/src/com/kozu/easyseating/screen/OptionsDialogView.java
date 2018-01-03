package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kozu.easyseating.EasySeatingGame;

/**
 * Created by Rob on 11/22/2017.
 */

public class OptionsDialogView extends AbstractLmlView {
    private Stage stage;
    public OptionsDialogView(Stage stage) {
        super(stage);
        this.stage = stage;
    }
    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public String getViewId() {
        return "100";
    }

    @LmlAction("navigateToMainMenu")
    public void navigateToMainMenu() {
        ((EasySeatingGame) Gdx.app.getApplicationListener()).navigateToMainMenu();
    }
}
