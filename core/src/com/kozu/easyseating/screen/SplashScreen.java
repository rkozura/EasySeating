package com.kozu.easyseating.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kozu.easyseating.EasySeatingGame;

/**
 * Loads assets and renders a loading bar.  When all assets are loaded, set screen to seating screen
 *
 * Created by Rob on 8/11/2017.
 */
public class SplashScreen extends ScreenAdapter {
    private AssetManager manager;
    private Game game;
    private TextureAtlas atlas;
    private Stage stage;
    private ProgressBar loadingBar;

    public SplashScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        manager = new AssetManager();

        //Load the vector tff font file
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        manager.load("customskin.atlas", TextureAtlas.class);

        FreetypeFontLoader.FreeTypeFontLoaderParameter mySmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        mySmallFont.fontFileName = "font.ttf";
        mySmallFont.fontParameters.size = 24;
        manager.load("font.ttf", BitmapFont.class, mySmallFont);


        Pixmap pixmap = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = drawable;

        pixmap = new Pixmap(0, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knob = drawable;

        pixmap = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knobBefore = drawable;


        loadingBar = new ProgressBar(0.0f, 1.0f, 0.01f, false, progressBarStyle);
        loadingBar.setValue(0.0f);
        loadingBar.setAnimateDuration(0.05f);
        loadingBar.setBounds(10, 10, 100, 20);

        stage.addActor(loadingBar);
    }

    @Override
    public void render(float delta) {
        if (manager.update()) {
            atlas = manager.get("customskin.atlas", TextureAtlas.class);

            Skin skin = new Skin();
            skin.add("normaltext", manager.get("font.ttf", BitmapFont.class), BitmapFont.class);

            skin.addRegions(atlas);
            skin.load(Gdx.files.internal("customskin.json"));

            EasySeatingGame.uiSkin = skin;

            SeatingScreen seatingScreen = new SeatingScreen();
            game.setScreen(seatingScreen);
        } else {
            loadingBar.setValue(manager.getProgress());
            stage.act();
            stage.draw();
        }
    }

    @Override
    public void dispose() {
        atlas.dispose();
        manager.dispose();
        super.dispose();
    }
}
