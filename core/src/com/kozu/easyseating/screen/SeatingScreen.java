package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.controller.SeatingController;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.renderer.SeatingRenderer;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.UILogic;

public class SeatingScreen extends ScreenAdapter {
    OrthographicCamera camera;
    private  SeatingRenderer renderer;
    private Viewport viewport;
    private GestureDetector gestureDetector;
    private SeatingLogic seatingLogic;

    public SeatingScreen() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        //Create the camera and apply a viewport
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        //Create the renderer.  Renderer needs to see the logic to know what to render
        renderer = new SeatingRenderer();

        //Setup the controller, which listens for gestures
        gestureDetector = new GestureDetector(new SeatingController(camera));
    }

    @Override
    public void show() {
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(UILogic.stage);
        multi.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(multi);
    }

    @Override
    public void render(float delta) {
        camera.update();
        TweenUtil.getTweenManager().update(delta);

        //Clear the color and depth buffer so screen repaints.  Depth buffer is responsible for
        //removing table texture outside table circle
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        EasySeatingGame.batch.setProjectionMatrix(camera.combined);

        renderer.render();

        //Update (act) and draw the UI after everything else
        UILogic.stage.act();
        UILogic.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        UILogic.stage.getViewport().update(width, height);
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }
}