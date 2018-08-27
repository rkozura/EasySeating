package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.kotcrab.vis.ui.VisUI;

/**
 * Hides the dialog if clicked outside of it
 * Hides any open keyboards
 *
 * Created by Rob on 8/12/2017.
 */
public class DialogSize extends ReflectedLmlDialog {
    private Button closeButton;

    public DialogSize(final String title, final Skin skin, final String windowStyleName) {
        super(title, skin, windowStyleName);
        initialize();
    }

    private void initialize() {
        //Allow room for the label text
        padTop(.36f* Gdx.graphics.getPpiX());

        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    if (DialogSize.this.getColor().a == 1) {
                        hide();
                    }
                }
            }
        });

        closeButton = new Button(VisUI.getSkin(), "close");
        closeButton.setSize(.25f* Gdx.graphics.getPpiX(), .25f* Gdx.graphics.getPpiY());
        closeButton.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                DialogSize.this.hide();
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        closeButton.getColor().a = getColor().a;
        //TODO find a way to set the position outside the draw method
        closeButton.setPosition(getX()+getWidth(), getY()+getHeight());
        closeButton.draw(batch, parentAlpha);
    }

    @Override
    public void hide() {
        //Always hide the onscreen keyboard when closing dialogs
        Gdx.input.setOnscreenKeyboardVisible(false);
        super.hide();
    }
}
