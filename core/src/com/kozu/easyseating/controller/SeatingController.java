package com.kozu.easyseating.controller;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.ui.UILogic;

/**
 * Created by Rob on 8/4/2017.
 */

public class SeatingController implements GestureDetector.GestureListener {

    private Camera camera;
    private SeatingLogic seatingLogic;

    public SeatingController(Camera camera, SeatingLogic seatingLogic) {
        this.camera = camera;
        this.seatingLogic = seatingLogic;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    /**
     * A tap will either:
     * -Add a new table
     * -Move a selected table
     * -Add a person to a table
     *
     * @param x
     * @param y
     * @param count
     * @param button
     * @return
     */
    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 pos = convertScreenCoordsToWorldCoords(x, y);

        if(seatingLogic.selectedTable != null) {
            seatingLogic.moveTableToPosition(seatingLogic.selectedTable, pos);
        } else {
            Table table = seatingLogic.getTableAtPosition(pos);
            if(table != null) {
                seatingLogic.tappedTable = table;
                UILogic.showUI(seatingLogic);
            } else {
                seatingLogic.tappedTable = null;
                UILogic.hideUI();
                seatingLogic.addTableAtPosition(pos);
            }
        }

        return true;
    }

    /**
     * Long presses selected and deselect tables
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean longPress(float x, float y) {
        Vector3 pos = convertScreenCoordsToWorldCoords(x, y);

        Table table = seatingLogic.getTableAtPosition(pos);
        if(table != null) {
            seatingLogic.selectedTable = table;
        } else {
            seatingLogic.selectedTable = null;
        }

        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    /**
     * Pan the camera around the conference
     *
     * @param x
     * @param y
     * @param deltaX
     * @param deltaY
     * @return
     */
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
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    /**
     * Converts touch coords to game coords
     *
     * @param x
     * @param y
     * @return
     */
    private Vector3 convertScreenCoordsToWorldCoords(float x, float y) {
        Vector3 touchPos = new Vector3(x, y, 0);
        camera.unproject(touchPos);

        return touchPos;
    }
}
