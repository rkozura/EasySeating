package com.kozu.easyseating;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.screen.SplashScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.EntityAccessor;

import aurelienribon.tweenengine.Tween;

public class EasySeatingGame extends Game {

    public static Batch batch;
    public static Skin skin;
    public static Skin uiSkin;

    @Override
    public void create() {
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("data/uiskin.json")); //TODO Create uiskin.json file!

        Tween.registerAccessor(Camera.class, new CameraAccessor());
        Tween.registerAccessor(Table.class, new EntityAccessor());
        Tween.registerAccessor(Person.class, new EntityAccessor());
        Tween.setCombinedAttributesLimit(4);

        SplashScreen splashScreen = new SplashScreen(this);
        setScreen(splashScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}
