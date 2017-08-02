package com.kozu.easyseating;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kozu.easyseating.screen.SeatingScreen;

public class EasySeatingGame extends Game {

    public static Batch batch;
    public static Skin skin;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //skin = new Skin(Gdx.files.internal("data/uiskin.json")); //Create uiskin.json file!

        SeatingScreen seatingScreen = new SeatingScreen();
        setScreen(seatingScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
