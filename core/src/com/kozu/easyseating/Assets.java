package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/18/2017.
 */

public class Assets {
    private static final int BUTTON_FONT_SIZE = 25;
    private static final int DIALOG_FONT_SIZE = 35;
    private static final int MAIN_SCREEN_FONT_SIZE = 55;

    public AssetManager manager;

    //VisSkin
    public static AssetDescriptor<Skin> uiSkin;

    //Fonts
    public static AssetDescriptor<BitmapFont> buttontext;
    public static AssetDescriptor<BitmapFont> dialogtext;
    public static AssetDescriptor<BitmapFont> mainmenutext;

    //Textures
    public static final AssetDescriptor<Texture> tabletexture = new AssetDescriptor<Texture>("images/game/lightpaperfibers.png", Texture.class);
    public static final AssetDescriptor<Texture> floortexture = new AssetDescriptor<Texture>("images/game/floor.png", Texture.class);

    //Main menu backgrounds
    public static final List<AssetDescriptor<Texture>> backgroundtextures = new ArrayList<AssetDescriptor<Texture>>();

    public void init() {
        FreetypeFontLoader.FreeTypeFontLoaderParameter buttonFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        buttonFontParameter.fontParameters.size = (int) (BUTTON_FONT_SIZE * Gdx.graphics.getDensity());
        buttonFontParameter.fontFileName = "fonts/OpenSans-Regular.ttf";
        buttontext = new AssetDescriptor<BitmapFont>(buttonFontParameter.fontFileName, BitmapFont.class, buttonFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter dialogTitleFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        dialogTitleFontParameter.fontParameters.size = (int) (DIALOG_FONT_SIZE * Gdx.graphics.getDensity());
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

        manager.load(tabletexture);
        manager.load(floortexture);

        for(AssetDescriptor backgroundDescriptor : backgroundtextures) {
            manager.load(backgroundDescriptor);
        }

        //Finish loading before loading the skins.  Skin cannot be loaded until fonts referenced in
        //json are generated
        manager.finishLoading();
        ObjectMap<String, Object> resources = new ObjectMap<String, Object>();
        resources.put("default-font", manager.get(buttontext));
        resources.put("dialog-font", manager.get(dialogtext));
        resources.put("main-screen-font", manager.get(mainmenutext));
        SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("uiskin.atlas", resources);
        uiSkin = new AssetDescriptor<Skin>("uiskin.json", Skin.class, skinParameter);

        manager.load(uiSkin);
    }

    public void dispose() {
        manager.dispose();
    }

}
