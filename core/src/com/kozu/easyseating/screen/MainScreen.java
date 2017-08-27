package com.kozu.easyseating.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends ScreenAdapter {
    private Stage uiStage;

    public MainScreen(final Game game) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        uiStage = new Stage(new ScreenViewport());

        Image image = new Image(uiSkin.getPatch("blue_button06"));
        image.setFillParent(true);

        uiStage.addActor(image);

        Table table = new Table(uiSkin);
        table.setFillParent(true);
        uiStage.addActor(table);

        TextButton newButton = new TextButton("New",uiSkin);
        newButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SeatingScreen());
            }
        });

        table.add(newButton);
        table.add(new TextButton("Continue",uiSkin));
        table.add(new TextButton("Load",uiSkin));
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
