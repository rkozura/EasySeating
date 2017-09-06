package com.kozu.easyseating.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

import static com.kozu.easyseating.EasySeatingGame.skin;
import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
    private Stage uiStage;
    private Game game;

    public MainScreen() {
        super(new Stage());
    }

    @Override
    public void show() {
        super.show();

//        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
//        Image image = new Image(core.getParser().getData().getSkin("custom").getPatch("blue_button06"));
//        image.setFillParent(true);
//
//        getStage().addActor(image);
    }

    private void createVenueDialog() {
        final DialogSize dialog = new DialogSize(400f, 200f, uiSkin, "dialog");

        //Set the title table
        Table titleTable = dialog.getTitleTable();
        titleTable.clear();
        titleTable.add(new Label("   Venue Name   ", uiSkin)).padTop(70);

        //Set the content table
        Table contentTable = dialog.getContentTable();
        contentTable.clear();
        final TextField newVenueName = new TextField("", skin);
        newVenueName.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if (!focused)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                else
                    Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
        contentTable.add(newVenueName).colspan(2);
        contentTable.row();
        final TextButton createNewPersonButton = new TextButton("Go!", uiSkin);
        createNewPersonButton.addListener(new ChangeListener() {
                                              @Override
                                              public void changed(ChangeEvent event, Actor actor) {
                                                  newVenueName.setText(newVenueName.getText().trim());
                                                  if (!StringUtils.isBlank(newVenueName.getText())) {
                                                      game.setScreen(new SeatingScreen(newVenueName.getText(), game));
                                                  }
                                                  newVenueName.setText("");
                                                  dialog.hide();
                                              }
                                          }
        );

        contentTable.add(createNewPersonButton);
        final TextButton cancelButton = new TextButton("Cancel", uiSkin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newVenueName.setText("");
                dialog.hide();
            }
        });
        contentTable.add(cancelButton);

        dialog.show(uiStage);
        dialog.setY(uiStage.getHeight());

        uiStage.setKeyboardFocus(newVenueName);
    }

    public DialogSize getDialogSize() {
        return new DialogSize(100, 100, EasySeatingGame.uiSkin, "dialog");
    }


    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/MainMenuView.lml");
    }

    @Override
    public String getViewId() {
        return "second";
    }
}
