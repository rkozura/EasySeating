package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.DialogLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.LmlApplicationListener;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.VisUI;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.resolver.PersonImporter;
import com.kozu.easyseating.screen.MainScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.SpriteAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;

import aurelienribon.tweenengine.Tween;

public class EasySeatingGame extends LmlApplicationListener {
    public static PersonImporter importer;
    public static Batch batch;
    public static Skin skin;
    public static Skin uiSkin;

    public static Skin visSkin;

    public Assets assets;

    public EasySeatingGame(PersonImporter importer) {
        this.importer = importer;
    }

    @Override
    protected LmlParser createParser() {
        return VisLml.parser()
                .tag(getDialogSizeProvider(), "dialogsize")
                .build();
    }

    @Override
    public void create() {
        assets = new Assets();
        assets.load();

        //Block until manager has finished loading..
        assets.manager.finishLoading();

        try {
            AssetManager manager = new AssetManager();
            manager.load("uiskin.atlas", TextureAtlas.class);
            manager.finishLoading();

            visSkin = new Skin();
            visSkin.addRegions(manager.get("uiskin.atlas", TextureAtlas.class));
            visSkin.add("default-font", assets.manager.get(assets.buttontext), BitmapFont.class);

            visSkin.add("dialog-font", assets.manager.get(assets.dialogtext), BitmapFont.class);
            visSkin.add("main-screen-font", assets.manager.get(assets.mainmenutext), BitmapFont.class);

            if(!VisUI.isLoaded()) {
                VisUI.load(visSkin);
            }

            visSkin.load(Gdx.files.internal("uiskin.json"));
            super.create();

            batch = new SpriteBatch();
            skin = new Skin(Gdx.files.internal("data/uiskin.json")); //TODO Create uiskin.json file!

            Tween.registerAccessor(Camera.class, new CameraAccessor());
            Tween.registerAccessor(Table.class, new EntityAccessor());
            Tween.registerAccessor(Person.class, new EntityAccessor());
            Tween.registerAccessor(Sprite.class, new SpriteAccessor());
            Tween.setCombinedAttributesLimit(4);

            setView(MainScreen.class);
        } finally {
            //assets.manager.dispose();
        }
    }

    @Override
    public void render() {
        //Always update the tween engine
        TweenUtil.getTweenManager().update(Gdx.graphics.getDeltaTime());

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }

    private static LmlTagProvider getDialogSizeProvider() {
        return new LmlTagProvider() {
            @Override
            public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
                return new DialogLmlTag(parser, parentTag, rawTagData){
                    @Override
                    protected Dialog getNewInstanceOfWindow(TextLmlActorBuilder builder) {
                        return new DialogSize(builder.getText(), getSkin(builder), builder.getStyleName());
                    }
                };
            }
        };
    }
}
