package com.kozu.easyseating.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.parser.LmlView;

/**
 * Created by Rob on 11/22/2017.
 */

public class OptionsDialogView implements LmlView {
    private Stage stage;
    public OptionsDialogView(Stage stage) {
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
}
