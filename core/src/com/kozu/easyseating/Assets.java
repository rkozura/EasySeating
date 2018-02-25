package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/18/2017.
 */

public class Assets {
    private static final int BUTTON_FONT_SIZE = 25;
    private static final int DIALOG_FONT_SIZE = 35;
    private static final int MAIN_SCREEN_FONT_SIZE = 55;
    private static final int PERSON_FONT_SIZE = 10;
    private static final int HELP_FONT_SIZE = 15;

    public AssetManager manager;

    //Fonts
    public static AssetDescriptor<BitmapFont> buttontext;
    public static AssetDescriptor<BitmapFont> dialogtext;
    public static AssetDescriptor<BitmapFont> mainmenutext;
    public static AssetDescriptor<BitmapFont> persontext;
    public static AssetDescriptor<BitmapFont> helptext;

    //Textures
    public static final AssetDescriptor<Texture> tabletexture = new AssetDescriptor<Texture>("images/game/lightpaperfibers.png", Texture.class);
    public static final AssetDescriptor<Texture> floortexture = new AssetDescriptor<Texture>("images/game/floor.png", Texture.class);

    //Main menu backgrounds
    public static final List<AssetDescriptor<Texture>> backgroundtextures = new ArrayList<AssetDescriptor<Texture>>();

    public void init() {
        FreeTypeFontGenerator.setMaxTextureSize(2048);

        FreetypeFontLoader.FreeTypeFontLoaderParameter buttonFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        buttonFontParameter.fontParameters.size = (int) (BUTTON_FONT_SIZE * Gdx.graphics.getDensity());
        buttonFontParameter.fontFileName = "fonts/OpenSans-Regular.ttf";
        buttontext = new AssetDescriptor<BitmapFont>(buttonFontParameter.fontFileName, BitmapFont.class, buttonFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter helpFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        helpFontParameter.fontParameters.size = (int) (HELP_FONT_SIZE * Gdx.graphics.getDensity());
        helpFontParameter.fontFileName = "fonts/OpenSans-Regular-3.ttf";
        helptext = new AssetDescriptor<BitmapFont>(helpFontParameter.fontFileName, BitmapFont.class, helpFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter dialogTitleFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        dialogTitleFontParameter.fontParameters.size = (int) (DIALOG_FONT_SIZE * Gdx.graphics.getDensity());
        dialogTitleFontParameter.fontParameters.borderWidth = 2f;
        dialogTitleFontParameter.fontParameters.borderColor = Color.BLACK;
        dialogTitleFontParameter.fontFileName = "fonts/Pacifico.ttf";
        dialogtext = new AssetDescriptor<BitmapFont>(dialogTitleFontParameter.fontFileName, BitmapFont.class, dialogTitleFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter mainScreenTitleFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        mainScreenTitleFontParameter.fontParameters.size = (int) (MAIN_SCREEN_FONT_SIZE * Gdx.graphics.getDensity());
        mainScreenTitleFontParameter.fontParameters.borderWidth = 1f;
        mainScreenTitleFontParameter.fontParameters.borderColor = Color.BLACK;
        mainScreenTitleFontParameter.fontParameters.shadowOffsetX = 10;
        mainScreenTitleFontParameter.fontParameters.shadowOffsetY = 10;
        mainScreenTitleFontParameter.fontParameters.shadowColor = Color.BLACK;
        mainScreenTitleFontParameter.fontFileName = "fonts/Courgette-Regular.ttf";
        mainmenutext = new AssetDescriptor<BitmapFont>(mainScreenTitleFontParameter.fontFileName, BitmapFont.class, mainScreenTitleFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter personFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        personFontParameter.fontParameters.size = (int) (PERSON_FONT_SIZE * Gdx.graphics.getDensity());
        personFontParameter.fontParameters.borderWidth = 1f;
        personFontParameter.fontParameters.borderColor = Color.BLACK;
        personFontParameter.fontFileName = "fonts/OpenSans-Regular-2.ttf";
        persontext = new AssetDescriptor<BitmapFont>(personFontParameter.fontFileName, BitmapFont.class, personFontParameter);

        //Find all the files in the backgrounds directory
        FileHandle dirHandle = Gdx.files.internal("images/backgrounds");
        for (FileHandle entry : dirHandle.list()) {
            backgroundtextures.add(new AssetDescriptor<Texture>(entry, Texture.class));
        }
    }

    public Assets() {
        manager = new AssetManager();

        //Set a custom font file loader for .ttf files, as well as generated fonts from ttf
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        init();
    }

    public void load() {
        manager.load(buttontext);
        manager.load(dialogtext);
        manager.load(mainmenutext);
        manager.load(persontext);
        manager.load(helptext);

        manager.load(tabletexture);
        manager.load(floortexture);

        for(AssetDescriptor backgroundDescriptor : backgroundtextures) {
            manager.load(backgroundDescriptor);
        }
    }

    public void dispose() {
        manager.dispose();
    }

}
