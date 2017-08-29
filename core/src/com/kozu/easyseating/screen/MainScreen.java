package com.kozu.easyseating.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

import static com.kozu.easyseating.EasySeatingGame.skin;
import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends ScreenAdapter {
    private Stage uiStage;
    private Game game;

    public MainScreen(final Game game) {
        this.game = game;

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        uiStage = new Stage(new ScreenViewport());

        Image image = new Image(uiSkin.getPatch("blue_button06"));
        image.setFillParent(true);

        uiStage.addActor(image);

        Table table = new Table();
        table.setFillParent(true);
        uiStage.addActor(table);

        TextButton newButton = new TextButton("New",uiSkin);
        newButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createVenueDialog();
            }
        });

        table.add(newButton);
        table.add(new TextButton("Continue",uiSkin));
        table.add(new TextButton("Load",uiSkin));

        Table helpTable = new Table();
        helpTable.setFillParent(true);
        TextButton helpButton = new TextButton("?", uiSkin, "circle");
        helpTable.add(helpButton).expand().bottom().right();

        uiStage.addActor(helpTable);
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
                                                      game.setScreen(new SeatingScreen(newVenueName.getText()));
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

    @Override
    public void show() {
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multi);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Update (act) and draw the UI after everything else
        uiStage.act();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height);
    }
}
