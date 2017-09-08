package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.controller.SeatingController;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.renderer.SeatingRenderer;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.UILogic;

import aurelienribon.tweenengine.Tween;

import static com.kozu.easyseating.EasySeatingGame.batch;

public class SeatingScreen extends AbstractLmlView {
    private OrthographicCamera camera;
    private SeatingRenderer renderer;
    private GestureDetector gestureDetector;
    private static SeatingLogic seatingLogic;

    public SeatingScreen() {
        super(new Stage());

        //Create the camera and apply a viewport
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        viewport = new ScreenViewport(camera);
//        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        camera.unproject(new Vector3(0, 0, 0));
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/SeatingView.lml");
    }

    @Override
    public String getViewId() {
        return "third";
    }

    public void setConferenceName(String conferenceName) {
        //Create the logic class...has methods to modify objects and return them
        seatingLogic = new SeatingLogic(conferenceName);

        UILogic uiLogic = new UILogic(seatingLogic);

        //Setup the controller, which listens for gestures
        gestureDetector = new GestureDetector(new SeatingController(camera, seatingLogic, uiLogic));

        //Create the renderer.  Renderer needs to see the logic to know what to render
        renderer = new SeatingRenderer(seatingLogic);
    }

    @Override
    public void show() {
        //Zoom the camera in from high to low.  Gives the user an overview of the seating
        camera.zoom = 3f;
        Tween.to(camera, CameraAccessor.ZOOM, 1.9f).target(1f)
                .start(TweenUtil.getTweenManager());

        GdxUtilities.setMultipleInputProcessors(Gdx.input.getInputProcessor(), gestureDetector);
    }

    @Override
    public void render(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        renderer.render();
        super.render(delta);

    }

    @LmlAction("openOptions")
    public void openOptions() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        //core.getParser().fillStage(getStage(), Gdx.files.internal("views/OptionsView.lml"));
        LmlView optionsView = core.getParser().createView(OptionsScreen.class, Gdx.files.internal("views/OptionsView.lml"));
        Array<Actor> s = core.getParser().createView(optionsView, Gdx.files.internal("views/OptionsView.lml"));

        LmlUtilities.appendActorsToStage(getStage(), s);
    }

    @LmlAction("export")
    public void export(VisTextButton visTextButton) {
        if(seatingLogic == null) {
            System.out.println("seatingLogic is null");
        } else {
            System.out.println("export");
        }
    }

    @Override
    public void resize(int width, int height) {
        //UILogic.stage.getViewport().update(width, height);
        //viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
        super.resize(width, height);
    }
}