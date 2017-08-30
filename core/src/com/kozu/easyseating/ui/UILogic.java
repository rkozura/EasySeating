package com.kozu.easyseating.ui;

import com.badlogic.gdx.Game;
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
import com.kozu.easyseating.pdf.PDFGenerator;
import com.kozu.easyseating.screen.MainScreen;

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

    private static VerticalGroup tableScrollPeople;

    static Table venueButtons;

    SeatingLogic seatingLogic;

    public static boolean selectedPerson = false;

    private Game game;

    public UILogic(final SeatingLogic seatingLogic, Game game) {
        this.seatingLogic = seatingLogic;
        this.game = game;

        stage = new Stage(new ScreenViewport());
        tableScrollPeople = new VerticalGroup();
        tableScrollPeople.left();

        //Setup the screen wide UI
        //-Venue Button
        //-Options Button
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right();
        TextButton venueButton = new TextButton("Venue", uiSkin, "small");
        venueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showVenueDialog(seatingLogic);
            }
        });
        uiTable.add(venueButton);
        TextButton optionsButton = new TextButton("Options", uiSkin, "small");
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showOptionsDialog();
            }
        });
        uiTable.add(optionsButton).expandX().right();

        stage.addActor(uiTable);
    }

    public void showTableDialog(SeatingLogic seatingLogic, com.kozu.easyseating.object.Table table) {
        Dialog tableDialog = new DialogSize(200f, 400f, uiSkin, "dialog");

        //Set the content table
        Table contentTable = tableDialog.getContentTable();
        contentTable.clear();
        contentTable.defaults().pad(10);
        ScrollPane scrollPane = new ScrollPane(tableScrollPeople, skin);
        scrollPane.setScrollingDisabled(true, false);
        contentTable.add(scrollPane).fill().expand().colspan(2).padBottom(0).padTop(70);
        contentTable.row();
        Table newSortTable = new Table();
        newSortTable.defaults().pad(10);
        TextButton createPersonButton = new TextButton("New", uiSkin, "small");
        newSortTable.add(createPersonButton).width(75);
        newSortTable.add(new TextButton("Sort", uiSkin, "small")).width(75);
        contentTable.add(newSortTable);

        tableDialog.show(stage);

        //Set the dialog title to the table identifier
        Table titleTable = tableDialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Table "+table.tableIdentifier+"   "
                , uiSkin)).height(30).padTop(70);

        //Loop through the people and check which ones are sitting at the tapped table
        for(Actor person : tableScrollPeople.getChildren()) {
            PersonSelectorRow personSelectorRow = (PersonSelectorRow)person;
            personSelectorRow.selectPersonWithTable(table);
        }
        tableScrollPeople.getChildren().sort(new PersonSelectorRowComparators.PersonSelectorRowNameComparator());
        tableScrollPeople.invalidate();
        scrollPane.setScrollY(0);

        newPersonButtonAddListener(createPersonButton, seatingLogic, table);
    }

    public void showVenueDialog(SeatingLogic seatingLogic) {
        selectedPerson = false;

        Dialog venueDialog = new DialogSize(200f, 400f, uiSkin, "dialog");
        Table titleTable = venueDialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label(seatingLogic.conference.conferenceName, uiSkin)).padTop(70);
        //Set the content table
        Table venueDialogContentTable = venueDialog.getContentTable();
        venueDialogContentTable.clear();
        venueDialogContentTable.defaults().pad(10);
        ScrollPane venueScrollPane = new ScrollPane(tableScrollPeople, skin);
        venueScrollPane.setScrollingDisabled(true, false);
        venueDialogContentTable.add(venueScrollPane).fill().expand().colspan(2).padBottom(0).padTop(70);
        venueDialogContentTable.row();
        venueButtons = new Table();
        venueButtons.defaults().pad(10);
        venueDialogContentTable.add(venueButtons);

        for(Actor person : tableScrollPeople.getChildren()) {
            PersonSelectorRow personSelectorRow = (PersonSelectorRow)person;
            personSelectorRow.selectPersonWithVenue();
        }

        venueDialog.show(stage);

        addDefaultVenueButtons();
    }

    public void showOptionsDialog() {
        Dialog optionsDialog = new DialogSize(200f, 400f, uiSkin, "dialog");
        Table titleTable = optionsDialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("Options", uiSkin)).padTop(70);
        //Set the content table
        Table optionsDialogContentTable = optionsDialog.getContentTable();
        optionsDialogContentTable.clear();
        optionsDialogContentTable.defaults().pad(10);
        TextButton exportButton = new TextButton("Export", uiSkin, "small");
        exportButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PDFGenerator.generatePDF(seatingLogic);
            }
        });
        TextButton renameButton = new TextButton("Rename", uiSkin, "small");
        renameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                renameVenueDialog(seatingLogic);
            }
        });
        TextButton helpButton = new TextButton("Help", uiSkin, "small");
        TextButton exitButton = new TextButton("Main Menu", uiSkin, "small");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainScreen(game));
            }
        });
        optionsDialogContentTable.add(exportButton).row();
        optionsDialogContentTable.add(renameButton).row();
        optionsDialogContentTable.add(helpButton).row();
        optionsDialogContentTable.add(exitButton);

        optionsDialog.show(stage);
    }

    private void addDefaultVenueButtons() {
        TextButton createPersonVenueButton = new TextButton("New", uiSkin, "small");
        newPersonButtonAddListener(createPersonVenueButton, seatingLogic, null);

        venueButtons.clear();
        venueButtons.add(createPersonVenueButton).width(75);
        venueButtons.add(new TextButton("Sort", uiSkin, "small")).width(75);
    }

    public void showDeletePersonButton(final PersonSelectorRow personSelectorRow) {
        if(!selectedPerson) {
            selectedPerson = true;

            personSelectorRow.highlightRow();

            venueButtons.clear();
            TextButton deleteVenueButton = new TextButton("Delete", uiSkin, "small");
            venueButtons.add(deleteVenueButton).width(75);
            TextButton cancelVenueButton = new TextButton("Cancel", uiSkin, "small");
            venueButtons.add(cancelVenueButton).width(75);

            deleteVenueButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    seatingLogic.removePerson(personSelectorRow.person);
                    personSelectorRow.remove();

                    addDefaultVenueButtons();

                    selectedPerson = false;
                }
            });

            cancelVenueButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    personSelectorRow.unHighlightRow();

                    addDefaultVenueButtons();

                    selectedPerson = false;
                }
            });
        }
    }

    private void newPersonButtonAddListener(TextButton textButton, final SeatingLogic seatingLogic, final com.kozu.easyseating.object.Table table) {
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                createNewPersonDialog(seatingLogic, table);
            }
        });
    }

    private void createNewPersonDialog(final SeatingLogic seatingLogic, final com.kozu.easyseating.object.Table table) {
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
                  final Person person = seatingLogic.createPerson(newPersonName.getText().toUpperCase());

                  final PersonSelectorRow personSelectorRow = new PersonSelectorRow(person, seatingLogic, UILogic.this);
                  if(table != null) {
                      personSelectorRow.selectPersonWithTable(table);
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

    private void renameVenueDialog(final SeatingLogic seatingLogic) {
        final DialogSize dialog = new DialogSize(400f, 200f, uiSkin, "dialog");

        //Set the title table
        Table titleTable = dialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Venue Name   ", uiSkin)).padTop(70);

        //Set the content table
        Table contentTable = dialog.getContentTable();
        contentTable.clear();
        contentTable.add(new Label("   Name:   ", uiSkin));
        final TextField renameVenueName = new TextField("", skin);
        renameVenueName.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if (!focused)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                else
                    Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
        contentTable.add(renameVenueName);
        contentTable.row();
        final TextButton renameVenueButton = new TextButton("Rename", uiSkin);
        renameVenueButton.addListener(new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                  renameVenueName.setText(renameVenueName.getText().trim());
                  if (StringUtils.isNotBlank(renameVenueName.getText())) {
                      seatingLogic.conference.conferenceName = renameVenueName.getText();
                  }
                  renameVenueName.setText("");
                  dialog.hide();
              }
          }
        );

        contentTable.add(renameVenueButton);
        final TextButton cancelButton = new TextButton("Cancel", uiSkin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                renameVenueName.setText("");
                dialog.hide();
            }
        });
        contentTable.add(cancelButton);

        dialog.show(stage);
        dialog.setY(stage.getHeight());

        stage.setKeyboardFocus(renameVenueName);
    }
}

