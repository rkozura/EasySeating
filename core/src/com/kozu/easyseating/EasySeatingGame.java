package com.kozu.easyseating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.tag.actor.DialogLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.LmlApplicationListener;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.resolver.PDFGenerator;
import com.kozu.easyseating.resolver.PersonImporter;
import com.kozu.easyseating.screen.MainScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.ColorAccessor;
import com.kozu.easyseating.tweenutil.EntityAccessor;
import com.kozu.easyseating.tweenutil.SpriteAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;

import aurelienribon.tweenengine.Tween;

public class EasySeatingGame extends LmlApplicationListener {
    public static PersonImporter importer;
    public static PDFGenerator pdfGenerator;
    public static Batch batch;

    public Assets assets;

    private MainScreen mainScreen;

    private Stage stage;
    private BitmapFont font;

    public EasySeatingGame(PersonImporter importer, PDFGenerator pdfGenerator) {
        this.importer = importer;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    protected LmlParser createParser() {

        return VisLml.parser()
                .tag(getDialogSizeProvider(), "dialogsize")
                .action("ppi", new ActorConsumer<String, VisTable>() {
                    @Override
                    public String consume(VisTable actor) {
                        return String.valueOf(Gdx.graphics.getPpiX());
                    }
                })
                .build();
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);

        stage = new Stage(new StretchViewport(300, 300));

        font = new BitmapFont();
        font.setColor(Color.BLUE);

        assets = new Assets();
        assets.load();

        batch = new SpriteBatch();

        Tween.registerAccessor(Camera.class, new CameraAccessor());
        Tween.registerAccessor(Table.class, new EntityAccessor());
        Tween.registerAccessor(Person.class, new EntityAccessor());
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.setCombinedAttributesLimit(4);
    }

    boolean loaded = false;
    Skin visSkin;
    @Override
    public void render() {

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
        }

            //Always update the tween engine
        TweenUtil.getTweenManager().update(Gdx.graphics.getDeltaTime());

        super.render();

        if(!loaded && assets.manager.update()) {
            visSkin = new Skin(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
            visSkin.add("default-font", assets.manager.get(Assets.buttontext), BitmapFont.class);
            visSkin.add("dialog-font", assets.manager.get(Assets.dialogtext), BitmapFont.class);
            visSkin.add("main-screen-font", assets.manager.get(Assets.mainmenutext), BitmapFont.class);
            visSkin.add("person-font", assets.manager.get(Assets.persontext), BitmapFont.class);
            visSkin.add("help-font", assets.manager.get(Assets.helptext), BitmapFont.class);

            visSkin.load(Gdx.files.internal("uiskin.json"));

            if(!VisUI.isLoaded())
                VisUI.load(visSkin);

            loaded = true;

            super.create();

            mainScreen = new MainScreen(assets);

            getParser().createView(mainScreen, mainScreen.getTemplateFile());
            setView(mainScreen);
        } else if(!loaded) {
            batch.setProjectionMatrix(stage.getCamera().combined);
            batch.begin();
            font.draw(batch, "Loading..."+(int)(assets.manager.getProgress()*100)+"%", 0, 300);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (font != null) {
            font.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }

        if (visSkin != null) {
            visSkin.dispose();
        }

        if (assets != null && assets.manager != null) {
            assets.manager.dispose();
        }

        super.dispose();
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

    public void navigateToMainMenu() {
        setView(mainScreen);
    }
}
