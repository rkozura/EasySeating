package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kozu.easyseating.logic.SeatingLogic;

/**
 * Created by Rob on 8/5/2017.
 */

public class UILogic {
    private static final float BUTTON_WIDTH = Math.round(.5* Gdx.graphics.getPpiX());
    private static final float BUTTON_HEIGHT = Math.round(.5* Gdx.graphics.getPpiY());

    public static Stage stage;

    private static Table parentTable;
    private static Button createPerson;

    private static ChangeListener changeListener;

    static {
        stage = new Stage();

        parentTable = new Table();
        parentTable.setFillParent(true);
        parentTable.top();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();

        createPerson = new TextButton("Create person", textButtonStyle);
        parentTable.add(createPerson);

        hideUI();

        stage.addActor(parentTable);

        parentTable.debug(); //TODO remove me in production!
    }

    public static void showUI(final SeatingLogic seatingLogic) {
        createPerson.addListener(changeListener = new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                seatingLogic.addPersonToTable(seatingLogic.tappedTable);
            }
        });
        parentTable.setVisible(true);
    }

    public static void hideUI() {
        if(changeListener != null) {
            createPerson.removeListener(changeListener);
        }
        parentTable.setVisible(false);
    }
}

