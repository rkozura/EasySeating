package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/**
 * Created by Rob on 12/18/2017.
 */

public class Assets {
    private static final FreetypeFontLoader.FreeTypeFontLoaderParameter buttonFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
    private static final FreetypeFontLoader.FreeTypeFontLoaderParameter dialogTitleFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
    private static final FreetypeFontLoader.FreeTypeFontLoaderParameter mainScreenTitleFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
    private static final int BUTTON_FONT_SIZE = 25;
    private static final int DIALOG_FONT_SIZE = 35;
    private static final int MAIN_SCREEN_FONT_SIZE = 55;

    static {
        buttonFontParameter.fontParameters.size = (int) (BUTTON_FONT_SIZE * Gdx.graphics.getDensity());
        buttonFontParameter.fontFileName = "fonts/OpenSans-Regular.ttf";

        dialogTitleFontParameter.fontParameters.size = (int) (DIALOG_FONT_SIZE * Gdx.graphics.getDensity());
        dialogTitleFontParameter.fontFileName = "fonts/Pacifico.ttf";

        mainScreenTitleFontParameter.fontParameters.size = (int) (MAIN_SCREEN_FONT_SIZE * Gdx.graphics.getDensity());
        mainScreenTitleFontParameter.fontParameters.borderWidth = 1f;
        mainScreenTitleFontParameter.fontParameters.borderColor = Color.BLACK;
        mainScreenTitleFontParameter.fontParameters.shadowOffsetX = 10;
        mainScreenTitleFontParameter.fontParameters.shadowOffsetY = 10;
        mainScreenTitleFontParameter.fontParameters.shadowColor = Color.BLACK;
        mainScreenTitleFontParameter.fontFileName = "fonts/Courgette-Regular.ttf";
    }

    //Atlas, json, and packed image
    public static final AssetDescriptor<TextureAtlas> customSkin = new AssetDescriptor<TextureAtlas>("customskin.atlas", TextureAtlas.class);


    //Fonts
    public static final AssetDescriptor<BitmapFont> buttontext = new AssetDescriptor<BitmapFont>(buttonFontParameter.fontFileName, BitmapFont.class, buttonFontParameter);
    public static final AssetDescriptor<BitmapFont> dialogtext = new AssetDescriptor<BitmapFont>(dialogTitleFontParameter.fontFileName, BitmapFont.class, dialogTitleFontParameter);
    public static final AssetDescriptor<BitmapFont> mainmenutext = new AssetDescriptor<BitmapFont>(mainScreenTitleFontParameter.fontFileName, BitmapFont.class, mainScreenTitleFontParameter);


    public AssetManager manager;

    public Assets() {
        manager = new AssetManager();

        //Set a custom font file loader for .ttf files, as well as generated fonts from ttf
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    public void load() {
        //Load all the asset descriptors
        manager.load(customSkin);

        manager.load(buttontext);
        manager.load(dialogtext);
        manager.load(mainmenutext);
    }

    public void dispose() {
        manager.dispose();
    }

}
