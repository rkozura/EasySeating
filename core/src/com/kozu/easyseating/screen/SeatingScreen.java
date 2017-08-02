package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.entity.Table;

public class SeatingScreen extends ScreenAdapter {
    OrthographicCamera camera;
    Texture img;
    private Table table;

    public SeatingScreen() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        img = new Texture("badlogic.jpg");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        table = new Table();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        EasySeatingGame.batch.setProjectionMatrix(camera.combined);

        table.render();
//        EasySeatingGame.batch.begin();
//        EasySeatingGame.batch.draw(img, 0, 0);
//        EasySeatingGame.batch.end();
    }
}