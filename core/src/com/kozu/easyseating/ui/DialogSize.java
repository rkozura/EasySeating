package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Created by Rob on 8/12/2017.
 */

public class DialogSize extends Dialog {
    private float height, width;
    public DialogSize(float width, float height, Skin skin, String style) {
        super("", skin, style);
        this.width = width;
        this.height = height;
        initialize();
    }

    @Override
    public float getPrefWidth() {
        return width;
    }

    @Override
    public float getPrefHeight() {
        return height;
    }

    private void initialize() {
        setModal(true);
        setResizable(false);
        setMovable(false);


        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    setTouchable(Touchable.disabled);
                    hide();
                }
            }
        });
    }
}
