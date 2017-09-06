package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.LmlApplicationListener;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.VisUI;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.resolver.PersonImporter;
import com.kozu.easyseating.screen.SplashScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.ui.DialogSize;

import aurelienribon.tweenengine.Tween;

public class EasySeatingGame extends LmlApplicationListener {
    public static PersonImporter importer;
    public static Batch batch;
    public static Skin skin;
    public static Skin uiSkin;

    public EasySeatingGame(PersonImporter importer) {
        this.importer = importer;
    }

    @Override
    protected LmlParser createParser() {
        return VisLml.parser()
                .tag(getCustomTagProvider(), "dialogsize")
                .build();
    }

    @Override
    public void create() {
        VisUI.load(VisUI.SkinScale.X2);
        super.create();

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("data/uiskin.json")); //TODO Create uiskin.json file!

        Tween.registerAccessor(Camera.class, new CameraAccessor());
        Tween.registerAccessor(Table.class, new EntityAccessor());
        Tween.registerAccessor(Person.class, new EntityAccessor());
        Tween.setCombinedAttributesLimit(4);

        setView(SplashScreen.class);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private static LmlTagProvider getCustomTagProvider() {
        return new LmlTagProvider() {
            @Override
            public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
                return getCustomTag(parser, parentTag, rawTagData);
            }
        };
    }

    // Creates a custom Label that blinks.
    private static LmlTag getCustomTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new AbstractActorLmlTag(parser, parentTag, rawTagData) {
            @Override
            protected LmlActorBuilder getNewInstanceOfBuilder() {
                // Normally you don't have to override this method, but we want to support String constructor, so we
                // supply one of default, extended builders:
                return new TextLmlActorBuilder();
                // By using this builder, we're automatically support "text", "txt" and "value" attributes, which will
                // use #setText(String) method to modify the builder.
            }

            @Override
            protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
                // Safe to cast builder. Always the same object type as returned by getNewInstanceOfBuilder:
                return new DialogSize(100,100, getSkin(builder), builder.getStyleName());
            }

            @Override
            protected void handlePlainTextLine(final String plainTextLine) {
                getParser().throwErrorIfStrict("Labels cannot have children. Even the blinking ones.");
            }

            @Override
            protected void handleValidChild(final LmlTag childTag) {
                getParser().throwErrorIfStrict("Labels cannot have children. Even the blinking ones.");
            }
        };
    }
}
