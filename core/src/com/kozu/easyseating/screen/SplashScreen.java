package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kozu.easyseating.EasySeatingGame;

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
        manager = new AssetManager();

        //Load the vector tff font file
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        manager.load("customskin.atlas", TextureAtlas.class);

        FreetypeFontLoader.FreeTypeFontLoaderParameter normalFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        normalFont.fontFileName = "font.ttf";
        normalFont.fontParameters.size = 32;
        manager.load("font.ttf", BitmapFont.class, normalFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter smallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallFont.fontFileName = "font.ttf";
        smallFont.fontParameters.size = 12;
        manager.load("smallfont.ttf", BitmapFont.class, smallFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter largeFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        largeFont.fontFileName = "font.ttf";
        largeFont.fontParameters.size = 58;
        manager.load("largefont.ttf", BitmapFont.class, largeFont);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (manager.update()) {
            atlas = manager.get("customskin.atlas", TextureAtlas.class);

            Skin skin = new Skin();
            skin.add("normaltext", manager.get("font.ttf", BitmapFont.class), BitmapFont.class);
            skin.add("smalltext", manager.get("smallfont.ttf", BitmapFont.class), BitmapFont.class);
            skin.add("largetext", manager.get("largefont.ttf", BitmapFont.class), BitmapFont.class);

            skin.addRegions(atlas);
            skin.load(Gdx.files.internal("customskin.json"));

            EasySeatingGame.uiSkin = skin;

            loadingBar.setValue(manager.getProgress());
        } else {
            loadingBar.setValue(manager.getProgress());
        }
    }

    @LmlAction("loaded")
    public void loaded() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        core.getParser().getData().addSkin("custom", EasySeatingGame.uiSkin);
        AbstractLmlView view = core.getParser().createView(MainScreen.class, Gdx.files.internal("views/MainMenuView.lml"));
        core.setView(view);
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
