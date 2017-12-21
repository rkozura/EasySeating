package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;

/**
 * Loads assets and renders a loading bar.  When all assets are loaded, set screen to seating screen
 *
 * Created by Rob on 8/11/2017.
 */
public class SplashScreen extends AbstractLmlView {
    private AssetManager manager;
    private TextureAtlas atlas;

    @LmlActor("progressBar") private ProgressBar loadingBar;

    public SplashScreen() {
        super(new Stage());
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        loaded();
//        if (manager.update()) {
//            atlas = manager.get("customskin.atlas", TextureAtlas.class);
//
//            Skin skin = new Skin();
//            skin.add("normaltext", manager.get("font.ttf", BitmapFont.class), BitmapFont.class);
//            skin.add("smalltext", manager.get("smallfont.ttf", BitmapFont.class), BitmapFont.class);
//            skin.add("largetext", manager.get("largefont.ttf", BitmapFont.class), BitmapFont.class);
//
//            skin.addRegions(atlas);
//            skin.load(Gdx.files.internal("customskin.json"));
//
//            EasySeatingGame.uiSkin = skin;
//
//            loadingBar.setValue(manager.getProgress());
//        } else {
//            loadingBar.setValue(manager.getProgress());
//        }
    }

    @LmlAction("loaded")
    public void loaded() {
//        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
//        core.getParser().getData().addSkin("custom", EasySeatingGame.uiSkin);
//        core.setView(MainScreen.class);
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/LoadingView.lml");
    }

    @Override
    public String getViewId() {
        return "first";
    }

    @Override
    public void dispose() {
        atlas.dispose();
        manager.dispose();
        super.dispose();
    }
}
