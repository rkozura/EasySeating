package com.kozu.easyseating;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kozu.easyseating.screen.SplashScreen;

public class EasySeatingGame extends Game {

    public static Batch batch;
    public static Skin skin;
    public static Skin uiSkin;

    @Override
    public void create() {
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("data/uiskin.json")); //TODO Create uiskin.json file!

        SplashScreen splashScreen = new SplashScreen(this);
        setScreen(splashScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}
