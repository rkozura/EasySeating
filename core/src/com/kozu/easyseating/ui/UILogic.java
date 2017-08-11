package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

import java.util.Random;

import static com.kozu.easyseating.EasySeatingGame.skin;

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

    private static Table parentTable;
    private static Button createPersonButton;
    private static VerticalGroup scrollPeople;

    private static ChangeListener changeListener;

    static Dialog dialog;

    static {
        scrollPeople = new VerticalGroup();

        createPersonButton = new TextButton("Table 1", skin);
        TextureRegion bgTexture = new TextureRegion(new Texture(Gdx.files.internal("grayspace.png")), 0, 0, 1, 1);
        TextureRegionDrawable drawable = new TextureRegionDrawable(bgTexture);

        dialog = new Dialog("", skin, "dialog"){
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
        dialog.setResizable(true);
        dialog.setMovable(false);

        dialog.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > dialog.getWidth() || y < 0 || y > dialog.getHeight()){
                    dialog.hide();
                    createPersonButton.removeListener(changeListener);
                    return true;
                }
                return false;
            }
        });

        stage = new Stage(new ScreenViewport());

        Table titleTable = dialog.getTitleTable();
        titleTable.clear();
        titleTable.debug();
        titleTable.add(new TextButton("<--", skin)).width(20).height(20).expand().left();
        titleTable.add(createPersonButton).width(150).height(20);


        Table contentTable = dialog.getContentTable();
        contentTable.clear();
        contentTable.debug();
        contentTable.defaults().pad(10);
        contentTable.add(new ScrollPane(scrollPeople, skin)).fill().expand().colspan(2).padBottom(0);
        contentTable.row();
        Table newSortTable = new Table();
        newSortTable.add(new TextButton("New", skin)).expand();
        newSortTable.add(new TextButton("Sort", skin)).expand();
        contentTable.add(newSortTable).fill().colspan(2);







//        parentTable = new Table();
//        parentTable.setFillParent(true);
//        parentTable.pad(20);
//
//        stage.addActor(parentTable);
//
//        parentTable.defaults().pad(10);
//        parentTable.add(new TextButton("<--", skin)).width(20).height(20).padRight(0);
//        parentTable.add(createPersonButton).width(150).height(20);
//        parentTable.row();
//        parentTable.add(new ScrollPane(scrollPeople, skin)).fill().expandY().colspan(2);
//        parentTable.row();
//        Table newSortTable = new Table();
//        newSortTable.add(new TextButton("New", skin)).expand();
//        newSortTable.add(new TextButton("Sort", skin)).expand();
//        newSortTable.debug();
//        parentTable.add(newSortTable).fill().colspan(2);

        //Add a middle table that holds data
        Table middleColumnTable = new Table();
        middleColumnTable.setBackground(drawable);
        //parentTable.add(middleColumnTable);
        //Add new person button
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        //createPersonButton = new TextButton("Create person", textButtonStyle);
        //middleColumnTable.add(createPersonButton).fill();

        middleColumnTable.row();

        //Select person scroll list

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = drawable;
        //ScrollPane scrollBarPeople = ;
        //middleColumnTable.add(scrollBarPeople).prefSize(999);

        //Add an empty table on the right
        //addEmptyContainer(drawable);

       // hideUI();


        //parentTable.setDebug(true); //TODO remove me in production!
    }

    private static void addEmptyContainer(TextureRegionDrawable drawable) {
        Container container = new Container();
        container.setBackground(drawable, false);
        parentTable.add(container).prefSize(999);;
    }

    public static void showUI(final SeatingLogic seatingLogic) {
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

        createPersonButton.addListener(changeListener = new ChangeListener() {
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

                            scrollPeople.addActorAt(0, personCheckBox);
                        }
                    }
                });
            }
        });

        //parentTable.setVisible(true);
    }

    public static void hideUI() {
//        if(changeListener != null) {
//            createPersonButton.removeListener(changeListener);
//        }
//        parentTable.setVisible(false);
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

