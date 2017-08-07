package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

import java.util.Random;

/**
 * Created by Rob on 8/5/2017.
 */

public class UILogic {
    private static final float BUTTON_WIDTH = Math.round(.5* Gdx.graphics.getPpiX());
    private static final float BUTTON_HEIGHT = Math.round(.5* Gdx.graphics.getPpiY());

    public static Stage stage;

    private static Table parentTable;
    private static Button createPersonButton;
    private static VerticalGroup scrollPeople;

    private static ChangeListener changeListener;

    static {
        TextureRegion bgTexture = new TextureRegion(new Texture(Gdx.files.internal("grayspace.png")), 0, 0, 1, 1);
        TextureRegionDrawable drawable = new TextureRegionDrawable(bgTexture);

        stage = new Stage();

        parentTable = new Table();
        parentTable.setFillParent(true);

        //Add an empty frame on the left
        Container leftContainer = new Container();
        leftContainer.setBackground(drawable, false);
        parentTable.add(leftContainer).prefSize(999);;

        Table rightColumnTable = new Table();
        rightColumnTable.setBackground(drawable);
        parentTable.add(rightColumnTable);
        //Add new person button
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        createPersonButton = new TextButton("Create person", textButtonStyle);
        rightColumnTable.add(createPersonButton).fill();

        rightColumnTable.row();

        //Select person scroll list
        scrollPeople = new VerticalGroup();
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = drawable;
        ScrollPane scrollBarPeople = new ScrollPane(scrollPeople, scrollPaneStyle);
        rightColumnTable.add(scrollBarPeople).prefSize(999);

        Container rightContainer = new Container();
        rightContainer.setBackground(drawable, false);
        parentTable.add(rightContainer).prefSize(999);

        hideUI();

        stage.addActor(parentTable);
        parentTable.debug(); //TODO remove me in production!
    }

    public static void showUI(final SeatingLogic seatingLogic) {
        //Loop through the people and check which ones are sitting at the tapped table
        for(Actor person : scrollPeople.getChildren()) {
            PersonCheckBox personCheckBox = (PersonCheckBox)person;
            //Don't fire handlers when setting initial checked status
            personCheckBox.setProgrammaticChangeEvents(false);
            if(seatingLogic.tappedTable.assignedSeats.contains(personCheckBox.person)) {
                personCheckBox.setChecked(true);
            } else {
                personCheckBox.setChecked(false);
            }
            personCheckBox.setProgrammaticChangeEvents(true);
        }

        createPersonButton.addListener(changeListener = new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                final Person person = seatingLogic.createPerson(getSaltString());

                CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
                checkBoxStyle.font = new BitmapFont();
                checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(new Texture("checkmark.jpg")));
                final PersonCheckBox personCheckBox = new PersonCheckBox(person.getName(), checkBoxStyle);
                personCheckBox.person = person;

                personCheckBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(personCheckBox.isChecked()) {
                            seatingLogic.addPersonToTable(seatingLogic.tappedTable, person);
                        } else {
                            seatingLogic.removePersonFromTable(seatingLogic.tappedTable, person);
                        }
                    }
                });

                scrollPeople.addActor(personCheckBox);
            }
        });

        parentTable.setVisible(true);
    }

    public static void hideUI() {
        if(changeListener != null) {
            createPersonButton.removeListener(changeListener);
        }
        parentTable.setVisible(false);
    }

    /**
     * Creates a random string that is 18 characters long
     * @return
     */
    protected static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}

