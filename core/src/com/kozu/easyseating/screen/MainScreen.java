package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;
import com.kozu.easyseating.ui.PhotoCarousel;

import org.apache.commons.lang3.StringUtils;

import aurelienribon.tweenengine.Tween;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
    @LmlActor("createVenueDialog") private DialogSize createVenueDialog;

    @LmlActor("venueName") private VisTextField venueName;

    private ToastManager toastManager;

    private Viewport viewport;

    private PhotoCarousel photoCarousel;

    public MainScreen() {
        super(new Stage(new ScreenViewport()));

        viewport = new FillViewport(0, 0);

        photoCarousel = new PhotoCarousel(viewport);

        //Zoom it in so there is enough area to move the camera around without moving
        //the background texture off the screen
        ((OrthographicCamera)viewport.getCamera()).zoom = .8f;
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
            SeatingScreen seatingScreen = new SeatingScreen();
            seatingScreen.setConferenceName(venueName);

            Array<Actor> seatingView = core.getParser().createView(seatingScreen, seatingScreen.getTemplateFile());

            core.setView(seatingScreen);
            LmlUtilities.appendActorsToStage(seatingScreen.getStage(), seatingView);

            return ReflectedLmlDialog.HIDE;
        }
    }

    @LmlAction("openCreateVenueDialog")
    public void openCreateVenueDialog() {
        createVenueDialog.setVisible(true);
        createVenueDialog.show(getStage());
        createVenueDialog.toFront();

        createVenueDialog.setPosition(createVenueDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        getStage().setKeyboardFocus(venueName);
        Gdx.input.setOnscreenKeyboardVisible(true);
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
        super.resize(width, height, centerCamera);
    }

    @Override
    public void show() {
        Tween.to(viewport.getCamera(), CameraAccessor.POSITION_XY, 20f)
                .target(viewport.getCamera().position.x-100, viewport.getCamera().position.y-100)
                .repeatYoyo(-1, 2)
                .start(TweenUtil.getTweenManager());
        //TODO this will show the "allow access dialog"...why doesnt it show up in the adapater?
        EasySeatingGame.importer.getPersonList();
        super.show();
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

    @Override
    public void dispose() {
        photoCarousel.dispose();
        super.dispose();
    }
}
