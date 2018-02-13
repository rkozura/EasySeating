package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;

/**
 * Created by Rob on 01/3/2018.
 */

public class HelpDialogView extends AbstractLmlView {
    public HelpDialogView(Stage stage) {
        super(stage);
    }

    @Override
    public String getViewId() {
        return "102";
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/HelpDialog.lml");
    }
}
