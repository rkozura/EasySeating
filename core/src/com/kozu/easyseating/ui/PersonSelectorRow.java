package com.kozu.easyseating.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    public UILogic uiLogic;
    public Person person;

    public Label personLabel;
    public Label assignedTableLabel;
    public Image checkedImage;

    public Drawable defaultBackground;
    public Drawable highlightBackground;

    public PersonSelectorRow(Person person, SeatingLogic seatingLogic, UILogic uiLogic) {
        padLeft(20f);
        
        setTouchable(Touchable.enabled);
        this.seatingLogic = seatingLogic;
        this.uiLogic = uiLogic;
        this.person = person;

        assignedTableLabel = new Label("", uiSkin, "row");
        personLabel = new Label(person.getName(), uiSkin, "row");
        checkedImage = new Image(uiSkin.getDrawable("green_checkmark"));

        //Set the cell size to the checkmark size so the table doesnt try and resize itself
        checkedCell = add().prefWidth(checkedImage.getWidth()).prefHeight(checkedImage.getHeight());
        add(personLabel).growX();

        defaultBackground = getBackground();

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.GREEN);
        pm1.fill();
        highlightBackground = new TextureRegionDrawable(new TextureRegion(new Texture(pm1)));
    }

    public void selectPersonWithTable(final com.kozu.easyseating.object.Table table) {
        unHighlightRow();

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
        unHighlightRow();

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
                uiLogic.showDeletePersonButton(PersonSelectorRow.this);
            }
        });
    }

    public void highlightRow() {
        setBackground(highlightBackground);
    }

    public void unHighlightRow() {
        setBackground(defaultBackground);
    }
}
