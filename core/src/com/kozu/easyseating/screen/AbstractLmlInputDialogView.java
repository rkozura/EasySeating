package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kozu.easyseating.ui.DialogSize;

/**
 * Created by Rob on 1/16/2018.
 */

public abstract class AbstractLmlInputDialogView extends AbstractLmlView {
    public AbstractLmlInputDialogView(Stage stage) {
        super(stage);
    }

    public void moveToTop(DialogSize dialog) {
        dialog.setPosition(dialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));
    }

    public abstract DialogSize getDialog();
}
