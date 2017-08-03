package com.kozu.easyseating.logic;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.renderer.SeatingRenderer;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingLogic extends GestureDetector.GestureAdapter {
    Camera camera;
    SeatingRenderer renderer;

    public SeatingLogic(Camera camera, SeatingRenderer seatingRenderer) {
        this.camera = camera;
        this.renderer = seatingRenderer;
    }


    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 touchPos = new Vector3();
        touchPos.set(x, y, 0);
        camera.unproject(touchPos);

        Table table = new Table(touchPos.x, touchPos.y);
        renderer.tables.add(table);
        return true;
    }
}
