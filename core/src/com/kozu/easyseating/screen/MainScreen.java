package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;
import com.kozu.easyseating.ui.PhotoCarousel;

import org.apache.commons.lang3.StringUtils;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
    @LmlActor("createVenueDialog") private DialogSize createVenueDialog;

    @LmlActor("venueName") private VisTextField venueName;

    @LmlActor("continueButton") private VisTextButton continueButton;

    @LmlActor("loadButton") private VisTextButton loadButton;

    private ToastManager toastManager;

    private Viewport viewport;

    private PhotoCarousel photoCarousel;

    private Assets assets;

    public MainScreen(Assets assets) {
        super(new Stage(new ScreenViewport()));

        viewport = new FillViewport(0, 0);

        photoCarousel = new PhotoCarousel(viewport, assets);

        //Zoom it in so there is enough area to move the camera around without moving
        //the background texture off the screen
        ((OrthographicCamera)viewport.getCamera()).zoom = .8f;

        this.assets = assets;
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/MainMenuView.lml");
    }

    @Override
    public String getViewId() {
        return "second";
    }

    @LmlAction("checkForInvalidVenueName")
    public boolean checkForInvalidVenueName(final DialogSize dialog) {
        String venueName = ((VisTextField) LmlUtilities.getActorWithId(dialog, "venueName")).getText();
        if(StringUtils.isBlank(venueName)) {
            final ToastManager manager = getToastManager(dialog.getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Invalid Venue Name", 1.5f);
            manager.toFront();

            return ReflectedLmlDialog.CANCEL_HIDING;

        } else {
            EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
            SeatingScreen seatingScreen = new SeatingScreen(venueName, assets);
            core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());

            core.setView(seatingScreen);

            return ReflectedLmlDialog.HIDE;
        }
    }

    @LmlAction("openCreateVenueDialog")
    public void openCreateVenueDialog() {
        createVenueDialog.setVisible(true);
        createVenueDialog.show(getStage());
        createVenueDialog.toFront();

        createVenueDialog.setPosition(createVenueDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        //The following three lines are absolutely needed for correct input on mobile
        FocusManager.switchFocus(getStage(), venueName);
        getStage().setKeyboardFocus(venueName);
        Gdx.input.setOnscreenKeyboardVisible(true);
    }

    @LmlAction("openContinueVenue")
    public void openContinueVenue() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();

        try {
            SeatingScreen seatingScreen = new SeatingScreen(State.loadLast(), assets);

            core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());
            core.setView(seatingScreen);
        } catch(Exception e) {
            //TODO Show confirmation message that the data is corrupt and want to remove
            e.printStackTrace();
            //If there was an exception loading the conference, disable to the continue button
            continueButton.setDisabled(true);
            continueButton.setTouchable(Touchable.disabled);
        }
    }

    @LmlAction("openLoadDialog")
    public void openLoadDialog() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        LmlView venueListVIew = new VenueListView(getStage(), assets);
        Array<Actor> actors = core.getParser().createView(venueListVIew, Gdx.files.internal("views/VenueListView.lml"));

        final DialogSize dialog = (DialogSize)actors.get(0);
        dialog.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > dialog.getWidth() || y < 0 || y > dialog.getHeight()) {
                    if (!State.hasContinue()) {
                        continueButton.setDisabled(true);
                        continueButton.setTouchable(Touchable.disabled);
                    }

                    if (!State.hasLoad()) {
                        loadButton.setDisabled(true);
                        loadButton.setTouchable(Touchable.disabled);
                    }

                }

                return false;
            }
        });
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }

    @LmlAction("ppi")
    public float getPPI(final VisTable container) {
        return Gdx.graphics.getPpiX();
    }

    @Override
    public void resize(int width, int height, boolean centerCamera) {
        viewport.getCamera().position.set(width/2f, height/2f, 0);
        viewport.update(width, height, true);
        getStage().getViewport().update(width, height, true);

        for(Actor actor : getStage().getActors()) {
            if(actor instanceof DialogSize) {
                ((DialogSize)actor).hide();
            }
        }

        super.resize(width, height, centerCamera);
    }

    private void centerActorOnStage(Actor actor) {
        actor.setPosition(Math.round((getStage().getWidth() - actor.getWidth()) / 2),
                Math.round((getStage().getHeight())));
    }

    @Override
    public void show() {
        createCameraTween().start(TweenUtil.getTweenManager());

        if(State.hasContinue()) {
            continueButton.setDisabled(false);
            continueButton.setTouchable(Touchable.enabled);
            loadButton.setDisabled(false);
            loadButton.setTouchable(Touchable.enabled);
        }

        super.show();
    }

    private Tween createCameraTween() {
        //Find a random point to move to
        float angle = (float)(Math.random()*Math.PI*2);
        float x = (float)Math.cos(angle)*100;
        float y = (float)Math.sin(angle)*100;

        return Tween.to(viewport.getCamera(), CameraAccessor.POSITION_XY, 15f)
                .target(viewport.getCamera().position.x + x, viewport.getCamera().position.y + y)
                .repeatYoyo(1, 1)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        createCameraTween().start(TweenUtil.getTweenManager());
                    }
                });
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        EasySeatingGame.batch.setProjectionMatrix(viewport.getCamera().combined);

        EasySeatingGame.batch.begin();
        photoCarousel.draw();
        EasySeatingGame.batch.end();

        getStage().getViewport().apply();

        super.render(delta);
    }
}
