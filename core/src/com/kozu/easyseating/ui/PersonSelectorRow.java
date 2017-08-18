package com.kozu.easyseating.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;

import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/17/2017.
 */

public class PersonSelectorRow extends Table {
    public boolean checked = false;
    public Cell checkedCell;
    public SeatingLogic seatingLogic;
    public Person person;

    public Label personLabel;
    public Label assignedTableLabel;
    public Image checkedImage;

    public PersonSelectorRow(Person person, SeatingLogic seatingLogic) {
        padLeft(20f);
        
        setTouchable(Touchable.enabled);
        this.seatingLogic = seatingLogic;
        this.person = person;

        assignedTableLabel = new Label("", uiSkin, "row");
        personLabel = new Label(person.getName(), uiSkin, "row");
        checkedImage = new Image(uiSkin.getDrawable("green_checkmark"));
        System.out.println(checkedImage.getWidth());
        System.out.println(checkedImage.getPrefWidth());
        System.out.println(checkedImage.getMaxWidth());

        //Set the cell size to the checkmark size so the table doesnt try and resize itself
        checkedCell = add().prefWidth(checkedImage.getWidth()).prefHeight(checkedImage.getHeight());
        add(personLabel).growX();
        debug();
    }

    public void selectPersonWithTable(final com.kozu.easyseating.object.Table table) {
        if(table.assignedSeats.contains(person)) {
            checked = true;
            checkedCell.setActor(checkedImage);
        } else {
            for(com.kozu.easyseating.object.Table thisTable : seatingLogic.conference.getTables()) {
                if(thisTable.assignedSeats.contains(person)) {
                    assignedTableLabel.setText(thisTable.tableIdentifier);
                    checkedCell.setActor(assignedTableLabel).center();
                    break;
                }
            }
            checked = false;
        }

        clearListeners();
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(checked) {
                    checked = false;
                    checkedCell.setActor(null);
                    seatingLogic.removePersonFromTable(table, person);
                } else {
                    checked = true;
                    checkedCell.setActor(checkedImage);
                    seatingLogic.addPersonToTable(table, person);
                }
            }
        });
    }

    public void selectPersonWithVenue() {
        checkedCell.setActor(null);
        for(com.kozu.easyseating.object.Table thisTable : seatingLogic.conference.getTables()) {
            if(thisTable.assignedSeats.contains(person)) {
                assignedTableLabel.setText(thisTable.tableIdentifier);
                checkedCell.setActor(assignedTableLabel);
                break;
            }
        }

        clearListeners();
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seatingLogic.removePerson(person);
                remove();
            }
        });
    }
}
