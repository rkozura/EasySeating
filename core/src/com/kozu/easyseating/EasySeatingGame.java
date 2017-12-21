package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

        Skin visSkin = assets.manager.get(Assets.uiSkin);

        if(!VisUI.isLoaded()) {
            VisUI.load(visSkin);
        }

        super.create();

        batch = new SpriteBatch();

        Tween.registerAccessor(Camera.class, new CameraAccessor());
        Tween.registerAccessor(Table.class, new EntityAccessor());
        Tween.registerAccessor(Person.class, new EntityAccessor());
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Tween.setCombinedAttributesLimit(4);

        MainScreen mainScreen = new MainScreen(assets);
        getParser().createView(mainScreen, mainScreen.getTemplateFile());

        setView(mainScreen);
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
