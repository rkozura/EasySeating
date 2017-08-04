package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.renderer.SeatingRenderer;

public class SeatingScreen extends ScreenAdapter {
    OrthographicCamera camera;
    private  SeatingRenderer renderer;
    private Viewport viewport;
    private GestureDetector gestureDetector;
    private SeatingLogic seatingLogic;

    public SeatingScreen() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        seatingLogic = new SeatingLogic();
        renderer = new SeatingRenderer(seatingLogic);

        //Setup the gestures
        gestureDetector = new GestureDetector(new GestureDetector.GestureAdapter(){
            @Override
            public boolean tap(float x, float y, int count, int button) {
                seatingLogic.handleTap(convertScreenCoordsToWorldCoords(x, y));
                return true;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                camera.update();
                camera.position.add(
                        camera.unproject(new Vector3(0, 0, 0))
                                .add(camera.unproject(new Vector3(deltaX, deltaY, 0)).scl(-1f))
                );

                return true;
            }

            @Override
            public boolean longPress(float x, float y) {
                seatingLogic.handleLongPress(convertScreenCoordsToWorldCoords(x, y));
                return true;
            }
        });
    }

    @Override
    public void show() {
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(multi);
    }

    @Override
    public void render(float delta) {
        camera.update();
        seatingLogic.update(delta);

        //Clear the color and depth buffer so screen repaints.  Depth buffer is responsible for
        //removing table texture outside table circle
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        EasySeatingGame.batch.setProjectionMatrix(camera.combined);

        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }

    private Vector3 convertScreenCoordsToWorldCoords(float x, float y) {
        Vector3 touchPos = new Vector3(x, y, 0);
        camera.unproject(touchPos);

        return touchPos;
    }
}