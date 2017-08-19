package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

import org.apache.commons.lang3.StringUtils;

import static com.kozu.easyseating.EasySeatingGame.skin;
import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Displays the UI used to add people to the conference
 *
 * Created by Rob on 8/5/2017.
 */
public class UILogic {
    private static final float BUTTON_WIDTH = Math.round(.5* Gdx.graphics.getPpiX());
    private static final float BUTTON_HEIGHT = Math.round(.5* Gdx.graphics.getPpiY());

    public static Stage stage;

    private static TextButton createPersonButton = new TextButton("New", uiSkin, "small");
    private static TextButton createPersonVenueButton = new TextButton("New", uiSkin, "small");
    private static VerticalGroup tableScrollPeople;

    private static ChangeListener changeCreatePersonButtonListener;
    private static ScrollPane scrollPane;

    private static Dialog venueDialog;
    private static ScrollPane venueScrollPane;

    private static Dialog tableDialog;

    static {
        stage = new Stage(new ScreenViewport());
        tableScrollPeople = new VerticalGroup();
        tableScrollPeople.left();

        tableDialog = new DialogSize(200f, 400f, uiSkin, "dialog");


        //Set the content table
        Table contentTable = tableDialog.getContentTable();
        contentTable.clear();
        contentTable.defaults().pad(10);
        scrollPane = new ScrollPane(null, skin);
        scrollPane.setScrollingDisabled(true, false);
        contentTable.add(scrollPane).fill().expand().colspan(2).padBottom(0).padTop(70);
        contentTable.row();
        Table newSortTable = new Table();
        newSortTable.defaults().pad(10);
        newSortTable.add(createPersonButton).width(75);
        newSortTable.add(new TextButton("Sort", uiSkin, "small")).width(75);
        contentTable.add(newSortTable);







        venueDialog = new DialogSize(200f, 400f, uiSkin, "dialog");
        Table titleTable = venueDialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Venue   ", uiSkin)).padTop(70);
        //Set the content table
        Table venueDialogContentTable = venueDialog.getContentTable();
        venueDialogContentTable.clear();
        venueDialogContentTable.defaults().pad(10);
        venueScrollPane = new ScrollPane(null, skin);
        venueScrollPane.setScrollingDisabled(true, false);
        venueDialogContentTable.add(venueScrollPane).fill().expand().colspan(2).padBottom(0).padTop(70);
        venueDialogContentTable.row();
        Table newSortTable2 = new Table();
        newSortTable2.defaults().pad(10);
        newSortTable2.add(createPersonVenueButton).width(75);
        newSortTable2.add(new TextButton("Sort", uiSkin, "small")).width(75);
        venueDialogContentTable.add(newSortTable2);



        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right();
        TextButton venueButton = new TextButton("Venue", uiSkin, "small");
        venueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for(Actor person : tableScrollPeople.getChildren()) {
                    PersonSelectorRow personSelectorRow = (PersonSelectorRow)person;
                    personSelectorRow.selectPersonWithVenue();
                }
                venueScrollPane.setWidget(tableScrollPeople);
                venueDialog.show(stage);
                newPersonButtonAddListener(createPersonVenueButton, SeatingLogic.getInstance());
            }
        });
        uiTable.add(venueButton);
        TextButton optionsButton = new TextButton("Options", uiSkin, "small");
        uiTable.add(optionsButton).expandX().right();

        stage.addActor(uiTable);
    }



    public static void showUI() {
        scrollPane.setWidget(tableScrollPeople);
        tableDialog.show(stage);

        //Set the dialog title to the table identifier
        Table titleTable = tableDialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Table "+SeatingLogic.getInstance().tappedTable.tableIdentifier+"   "
                , uiSkin)).height(30).padTop(70);

        //Loop through the people and check which ones are sitting at the tapped table
        for(Actor person : tableScrollPeople.getChildren()) {
            PersonSelectorRow personSelectorRow = (PersonSelectorRow)person;
            personSelectorRow.selectPersonWithTable(SeatingLogic.getInstance().tappedTable);
        }
        tableScrollPeople.getChildren().sort(new PersonSelectorRowComparators.PersonSelectorRowNameComparator());
        tableScrollPeople.invalidate();
        scrollPane.setScrollY(0);

        newPersonButtonAddListener(createPersonButton, SeatingLogic.getInstance());
    }

    private static void newPersonButtonAddListener(TextButton textButton, final SeatingLogic seatingLogic) {
        if(changeCreatePersonButtonListener != null) {
            textButton.removeListener(changeCreatePersonButtonListener);
        }
        textButton.addListener(changeCreatePersonButtonListener = new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                createNewPersonDialog();
            }
        });
    }

    private static void createNewPersonDialog() {
        final DialogSize dialog = new DialogSize(400f, 200f, uiSkin, "dialog");

        //Set the title table
        Table titleTable = dialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   New Person   ", uiSkin)).padTop(70);

        //Set the content table
        Table contentTable = dialog.getContentTable();
        contentTable.clear();
        contentTable.add(new Label("   Name:   ", uiSkin));
        final TextField newPersonName = new TextField("", skin);
        newPersonName.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if (!focused)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                else
                    Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
        contentTable.add(newPersonName);
        contentTable.row();
        final TextButton createNewPersonButton = new TextButton("Create", uiSkin);
        createNewPersonButton.addListener(new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                  newPersonName.setText(newPersonName.getText().trim());
                  if (!StringUtils.isBlank(newPersonName.getText())) {
                      final Person person = SeatingLogic.getInstance().createPerson(newPersonName.getText().toUpperCase());

                      final PersonSelectorRow personSelectorRow = new PersonSelectorRow(person, SeatingLogic.getInstance());
                      if(SeatingLogic.getInstance().tappedTable != null) {
                          personSelectorRow.selectPersonWithTable(SeatingLogic.getInstance().tappedTable);
                      } else {
                          personSelectorRow.selectPersonWithVenue();
                      }
                      tableScrollPeople.left().fill().expand().addActorAt(0, personSelectorRow);
                  }
                  newPersonName.setText("");
                  dialog.hide();
              }
          }
        );

        contentTable.add(createNewPersonButton);
        final TextButton cancelButton = new TextButton("Cancel", uiSkin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newPersonName.setText("");
                dialog.hide();
            }
        });
        contentTable.add(cancelButton);

        dialog.show(stage);
        dialog.setY(stage.getHeight());

        stage.setKeyboardFocus(newPersonName);
    }
}

