package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
    private static VerticalGroup scrollPeople;

    private static ChangeListener changeCreatePersonButtonListener;
    private static ScrollPane scrollPane;

    private static Dialog dialogSize;

    static {
        stage = new Stage(new ScreenViewport());
        scrollPeople = new VerticalGroup();
        scrollPeople.left();

        dialogSize = new DialogSize(200f, 400f, uiSkin, "dialog");

        //Set the title table
        Table titleTable = dialogSize.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Table 1   ", uiSkin)).height(30).padTop(70);

        //Set the content table
        Table contentTable = dialogSize.getContentTable();
        contentTable.clear();
        contentTable.defaults().pad(10);
        scrollPane = new ScrollPane(scrollPeople, skin);
        scrollPane.setScrollingDisabled(true, false);
        contentTable.add(scrollPane).fill().expand().colspan(2).padBottom(0).padTop(70);
        contentTable.row();
        Table newSortTable = new Table();
        newSortTable.defaults().pad(10);
        newSortTable.add(createPersonButton).width(75);
        newSortTable.add(new TextButton("Sort", uiSkin, "small")).width(75);
        contentTable.add(newSortTable);
    }

    public static void showUI(final SeatingLogic seatingLogic) {
        dialogSize.setTouchable(Touchable.enabled);
        dialogSize.show(stage);

        //Set the dialog title to the table identifier
        Table titleTable = dialogSize.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Table "+seatingLogic.tappedTable.tableIdentifier+"   "
                , uiSkin)).height(30).padTop(70);

        //Loop through the people and check which ones are sitting at the tapped table
        for(Actor person : scrollPeople.getChildren()) {
            PersonCheckBox personCheckBox = (PersonCheckBox)person;
            //Don't fire handlers when setting initial checked status
            personCheckBox.setProgrammaticChangeEvents(false);
            if(seatingLogic.tappedTable.assignedSeats.contains(personCheckBox.person)) {
                personCheckBox.setChecked(true);
                personCheckBox.getImageCell().setActor(personCheckBox.defaultImageActor);
            } else {
                personCheckBox.setChecked(false);
                for(com.kozu.easyseating.object.Table table : seatingLogic.conference.getTables()) {
                    if(table.assignedSeats.contains(personCheckBox.person)) {
                        float prefWidth = personCheckBox.getImageCell().getPrefWidth();
                        Label personTableIdentifier = new Label(table.tableIdentifier, uiSkin, "nobackground");
                        personCheckBox.getImageCell().setActor(personTableIdentifier)
                                .prefWidth(prefWidth);
                        break;
                    }
                }
            }
            personCheckBox.setProgrammaticChangeEvents(true);
        }
        scrollPeople.getChildren().sort(new PersonCheckBoxComparators.PersonCheckBoxNameComparator());
        scrollPeople.invalidate();
        scrollPane.setScrollY(0);

        if(changeCreatePersonButtonListener != null) {
            createPersonButton.removeListener(changeCreatePersonButtonListener);
        }
        createPersonButton.addListener(changeCreatePersonButtonListener = new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                createNewPersonDialog(seatingLogic);
            }
        });
    }

    private static void createNewPersonDialog(final SeatingLogic seatingLogic) {
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

                      final PersonCheckBox personCheckBox = new PersonCheckBox(seatingLogic, person, skin);
                      scrollPeople.addActorAt(0, personCheckBox);
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

