package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;

/**
 * Created by Rob on 8/12/2017.
 */

public class DialogSize extends ReflectedLmlDialog {
    public DialogSize(final String title, final Skin skin, final String windowStyleName) {
        super(title, skin, windowStyleName);
        initialize();
    }

    private void initialize() {
        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    hide();
                }
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        setTouchable(Touchable.enabled);
        return super.show(stage);
    }

    @Override
    public void hide() {
        //Disable touching while playing hiding animation so input doesn't go to it
        setTouchable(Touchable.disabled);
        super.hide();
    }
}
