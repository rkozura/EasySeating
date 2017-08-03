package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.entity.Table;

public class SeatingScreen extends ScreenAdapter {
    OrthographicCamera camera;
    private Table table;
    private Viewport viewport;

    public SeatingScreen() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        table = new Table();
    }

    @Override
    public void render(float delta) {
        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        EasySeatingGame.batch.setProjectionMatrix(camera.combined);

        table.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }
}