package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;

/**
 * Created by Rob on 9/7/2017.
 */

public class OptionsScreen extends AbstractLmlView {

    public OptionsScreen() {
        super(new Stage());
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/SeatingView.lml");
    }

    @Override
    public String getViewId() {
        return "fourth";
    }

    @LmlAction("test")
    public void checkForInvalidVenueName() {
        System.out.println("test2");
    }

}