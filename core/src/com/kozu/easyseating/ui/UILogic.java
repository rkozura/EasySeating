package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

import static com.kozu.easyseating.EasySeatingGame.skin;
import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/5/2017.
 */

/**
 * Displays the UI used to add people to the conference
 */
public class UILogic {
    private static final float BUTTON_WIDTH = Math.round(.5* Gdx.graphics.getPpiX());
    private static final float BUTTON_HEIGHT = Math.round(.5* Gdx.graphics.getPpiY());

    public static Stage stage;

    private static TextButton createPersonButton;
    private static TextButton backButton;
    private static VerticalGroup scrollPeople;

    private static ChangeListener changeCreatePersonButtonListener;
    private static ChangeListener changeBackButtonListener;

    static Dialog dialog;

    static {
        stage = new Stage(new ScreenViewport());
        scrollPeople = new VerticalGroup();
        createPersonButton = new TextButton("New", skin);

        backButton = new TextButton("<--", skin);
        changeBackButtonListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideUI();
            }
        };
        backButton.addListener(changeBackButtonListener);
        dialog = new Dialog("", uiSkin, "dialog"){
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 200f;
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 400f;
            }
        };

        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setMovable(false);

        //dialog.debugAll();

        dialog.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (x < 0 || x > dialog.getWidth() || y < 0 || y > dialog.getHeight()) {
                    hideUI();
                }
            }
        });

        //Set the title table
        Table titleTable = dialog.getTitleTable();
        titleTable.clear();
        titleTable.add(backButton).width(25).height(20).left().padTop(70).padLeft(10);
        titleTable.add(new Label("   Table 1   ", uiSkin)).height(30).padTop(70);

        //Set the content table
        Table contentTable = dialog.getContentTable();
        contentTable.clear();
        contentTable.defaults().pad(10);
        contentTable.add(new ScrollPane(scrollPeople, skin)).fill().expand().colspan(2).padBottom(0).padTop(70);
        contentTable.row();
        Table newSortTable = new Table();
        newSortTable.add(createPersonButton).center();
        newSortTable.add(new TextButton("Sort", skin));
        contentTable.add(newSortTable).fill().colspan(2);
    }

    public static void showUI(final SeatingLogic seatingLogic) {
        dialog.setTouchable(Touchable.enabled);
        dialog.show(stage);

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
        scrollPeople.getChildren().sort(new PersonCheckBoxComparator());
        scrollPeople.invalidate();

        createPersonButton.addListener(changeCreatePersonButtonListener = new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                createPersonButton.setDisabled(true);

                final Table confirmPersonTable = new Table();
                TextButton.TextButtonStyle confirmNewPersonButtonStyle = new TextButton.TextButtonStyle();
                confirmNewPersonButtonStyle.font = new BitmapFont();
                final TextButton confirmNewPersonButton = new TextButton("OK", confirmNewPersonButtonStyle);

                final TextField newPersonName = new TextField("asd", skin);

                confirmPersonTable.add(confirmNewPersonButton);
                confirmPersonTable.add(newPersonName);
                scrollPeople.addActorAt(0, confirmPersonTable);

                confirmNewPersonButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        scrollPeople.removeActor(confirmPersonTable);
                        createPersonButton.setDisabled(false);

                        if (!newPersonName.getText().equals("")) {
                            final Person person = seatingLogic.createPerson(newPersonName.getText().toUpperCase());

                            final PersonCheckBox personCheckBox = new PersonCheckBox(person.getName(), skin);
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

                            scrollPeople.addActorAt(0, personCheckBox);
                        }
                    }
                });
            }
        });
    }

    public static void hideUI() {
        dialog.setTouchable(Touchable.disabled);
        dialog.hide();
        createPersonButton.removeListener(changeCreatePersonButtonListener);
    }
}

