package com.kozu.easyseating.controller;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.screen.SeatingScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import aurelienribon.tweenengine.Tween;

/**
 * Created by Rob on 8/4/2017.
 */

public class SeatingController implements GestureDetector.GestureListener {

    private Camera camera;
    private SeatingLogic seatingLogic;
    private SeatingScreen seatingScreen;

    public SeatingController(Camera camera, SeatingLogic seatingLogic, SeatingScreen seatingScreen) {
        this.camera = camera;
        this.seatingLogic = seatingLogic;
        this.seatingScreen = seatingScreen;
    }

    Person draggedPerson;
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Table table = seatingScreen.getEditTable();
        if(table != null) {
            Vector3 pos = convertScreenCoordsToWorldCoords(x, y);
            for(Person person : table.assignedSeats) {
                if(person.bounds.contains(pos.x, pos.y)) {
                    draggedPerson = person;
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * A tap will open the selected object detail dialog
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

        if(seatingScreen.moveTable != null) {
            seatingLogic.moveTableToPosition(seatingScreen.moveTable, pos);

            return true;
        } else {
            Table table = seatingLogic.getTableAtPosition(pos);
            if (table != null) {
                //seatingScreen.openTable(table);
                Tween.to(camera, CameraAccessor.POSITION_XY, .3f).target(table.bounds.x, table.bounds.y)
                        .start(TweenUtil.getTweenManager());
                Tween.to(camera, CameraAccessor.ZOOM, .3f).target(.5f)
                        .start(TweenUtil.getTweenManager());
                seatingScreen.editTable(table);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Long presses open creation dialog
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean longPress(float x, float y) {
        //TODO Only make long press work if it is within the world bounds!
        Vector3 pos = convertScreenCoordsToWorldCoords(x, y);

        Table table = seatingLogic.getTableAtPosition(pos);
        if(table == null) {
            seatingScreen.openCreateObjectDialog(pos);
            return true;
        } else {
            seatingScreen.enableMoveTable(table);
            return true;
        }
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
        Vector3 vec = camera.unproject(new Vector3(0, 0, 0))
                .add(camera.unproject(new Vector3(deltaX, deltaY, 0)).scl(-1f));

        if(draggedPerson != null) {
            draggedPerson.bounds.x -= vec.x;
            draggedPerson.bounds.y -= vec.y;
        } else {
            camera.position.add(vec);
            camera.update();
        }

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        draggedPerson = null;

        float xx = MathUtils.clamp(camera.position.x, 0, (float)seatingLogic.conference.conferenceWidth);
        float yy = MathUtils.clamp(camera.position.y, 0, (float)seatingLogic.conference.conferenceHeight);

        if(xx != camera.position.x || yy != camera.position.y) {
            Tween.to(camera, CameraAccessor.POSITION_XY, .3f).target(xx, yy)
                    .start(TweenUtil.getTweenManager());
        }

        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    private Vector2 oldInitialFirstPointer=null, oldInitialSecondPointer=null;
    private float oldScale;
    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
        if(!(initialFirstPointer.equals(oldInitialFirstPointer)&&initialSecondPointer.equals(oldInitialSecondPointer))){
            oldInitialFirstPointer = initialFirstPointer.cpy();
            oldInitialSecondPointer = initialSecondPointer.cpy();
            oldScale = ((OrthographicCamera)camera).zoom;
        }
        Vector3 center = new Vector3(
                (firstPointer.x+initialSecondPointer.x)/2,
                (firstPointer.y+initialSecondPointer.y)/2,
                0
        );
        zoomCamera(center, oldScale * initialFirstPointer.dst(initialSecondPointer) / firstPointer.dst(secondPointer));

        return true;
    }

    @Override
    public void pinchStop() {

    }

    private void zoomCamera(Vector3 origin, float scale){
        camera.update();
        Vector3 oldUnprojection = camera.unproject(origin.cpy()).cpy();
        ((OrthographicCamera)camera).zoom = scale; //Larger value of zoom = small images, border view
        ((OrthographicCamera)camera).zoom = Math.min(2.0f, Math.max(((OrthographicCamera)camera).zoom, 0.5f));
        camera.update();
        Vector3 newUnprojection = camera.unproject(origin.cpy()).cpy();
        camera.position.add(oldUnprojection.cpy().add(newUnprojection.cpy().scl(-1f)));
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
