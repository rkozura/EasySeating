package com.kozu.easyseating.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.logic.State;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.screen.SeatingScreen;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import aurelienribon.tweenengine.Tween;

/**
 * Created by Rob on 8/4/2017.
 */

public class SeatingController extends GestureDetector {
    private float LONG_PRESS_SECONDS = .5f;
    private Camera camera;
    private SeatingLogic seatingLogic;

    SeatingControllerListener listener;
    public SeatingController(Camera camera, SeatingLogic seatingLogic, SeatingScreen seatingScreen){
        super(new SeatingControllerListener(camera, seatingLogic, seatingScreen));
        setLongPressSeconds(LONG_PRESS_SECONDS);
        this.camera = camera;
        this.seatingLogic = seatingLogic;
    }

    /**
     * Pans the screen when dragging a person.  This method should be called in the render method of
     * the screen.
     */
    public void personPan() {
        if(SeatingControllerListener.draggedPerson != null && isPanning()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();

            float width = Gdx.app.getGraphics().getWidth();
            float height = Gdx.app.getGraphics().getHeight();
            float space = Gdx.graphics.getPpiX()*.4f; //How much to detect a space between the edges of the screen

            boolean panned = false;
            if(touchX >= width-space) {
                camera.position.add(space*.1f, 0, 0);
                panned = true;
            } else if(touchX <= space) {
                camera.position.sub(space*.1f, 0, 0);
                panned = true;
            }

            if(touchY >= height-space) {
                camera.position.sub(0, space*.1f, 0);
                panned = true;
            } else if(touchY <= space) {
                camera.position.add(0, space*.1f, 0);
                panned = true;
            }

            if(panned) {
                camera.position.x = MathUtils.clamp(camera.position.x, 0, (float) seatingLogic.conference.conferenceWidth);
                camera.position.y = MathUtils.clamp(camera.position.y, 0, (float) seatingLogic.conference.conferenceHeight);

                Vector3 pos = camera.unproject(new Vector3(touchX, touchY, 0));
                SeatingControllerListener.draggedPerson.bounds.x = pos.x;
                SeatingControllerListener.draggedPerson.bounds.y = pos.y;
            }
        }
    }

    public void cancelDragPerson() {
        SeatingControllerListener.draggedPerson = null;
    }
}

class SeatingControllerListener implements GestureDetector.GestureListener {

    private Camera camera;
    private SeatingLogic seatingLogic;
    private SeatingScreen seatingScreen;

    public SeatingControllerListener(Camera camera, SeatingLogic seatingLogic, SeatingScreen seatingScreen) {
        this.camera = camera;
        this.seatingLogic = seatingLogic;
        this.seatingScreen = seatingScreen;
    }

    static Person draggedPerson;
    static Table draggedTable;
    private Table table;
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Vector3 pos = convertScreenCoordsToWorldCoords(x, y);
        if(SeatingScreen.addRemoveTable) {
            //If adding or removing tables, drag table
            Table table = seatingLogic.getTableAtPosition(pos);
            if(table != null) {
                draggedTable = table;
                return true;
            } else {
                return false;
            }
        } else {
            for(Table table : seatingLogic.conference.getTables()) {
                for (Person person : table.assignedSeats) {
                    if (person.bounds.contains(pos.x, pos.y)) {
                        draggedPerson = person;
                        this.table = table;
                        return true;
                    }
                }
            }
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

        draggedTable = null;
        if(seatingScreen.addRemoveTable) {
            if(seatingScreen.removeTable) {
                Table table = seatingLogic.getTableAtPosition(pos);
                if(table != null) {
                    seatingLogic.removeTable(table);
                } else {
                    return false;
                }
            } else {
                seatingLogic.addTableAtPosition(pos);
            }

            return true;
        } else if(seatingScreen.getEditTable() != null) {
            //Check if another table was tapped and set it as the edit table
            Table tappedTable = seatingLogic.getTableAtPosition(pos);
            if (tappedTable != null) {
                TweenUtil.getTweenManager().killTarget(camera);
                Tween.to(camera, CameraAccessor.POSITION_XY, .3f).target(tappedTable.bounds.x, tappedTable.bounds.y)
                        .start(TweenUtil.getTweenManager());

                float zoom = calculateZoom(tappedTable);
                Tween.to(camera, CameraAccessor.ZOOM, .3f).target(zoom)
                        .start(TweenUtil.getTweenManager());
                seatingScreen.editTable(tappedTable);
                return true;
            } else {
                Table table = seatingScreen.getEditTable();
                Person person = seatingLogic.getPersonAtPosition(pos, table);
                if(person != null) {
                    person.setFlaggedForRemoval(!person.isFlaggedForRemoval());
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            Table table = seatingLogic.getTableAtPosition(pos);
            if (table != null) {
                TweenUtil.getTweenManager().killTarget(camera);
                Tween.to(camera, CameraAccessor.POSITION_XY, .3f).target(table.bounds.x, table.bounds.y)
                        .start(TweenUtil.getTweenManager());

                float zoom = calculateZoom(table);
                Tween.to(camera, CameraAccessor.ZOOM, .3f).target(zoom)
                        .start(TweenUtil.getTweenManager());
                seatingScreen.editTable(table);
                return true;
            } else {
                return false;
            }
        }
    }

    private float calculateZoom(Table table) {
        Vector3 vecXY = worldToScreenCoords(table.getX(), table.getY());
        Vector3 vecRadius = worldToScreenCoords(table.getX()+(table.getRadius()*2), table.getY());

        float tableScreenSize = Math.abs(vecXY.x - vecRadius.x);
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float smallestScreenDimension = screenWidth < screenHeight ? screenWidth : screenHeight;

        float ratio = tableScreenSize/(smallestScreenDimension-(Gdx.graphics.getPpiX()*1.5f));

        return ((OrthographicCamera)camera).zoom * ratio;
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
        seatingScreen.showAddRemoveTablesTable();
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
        Vector3 vec2 = camera.unproject(new Vector3(x, y, 0));

        if(draggedPerson != null) {
            draggedPerson.setFlaggedForRemoval(false);
            draggedPerson.bounds.x = vec2.x;
            draggedPerson.bounds.y = vec2.y;

            boolean foundTable = false;
            for(Table table : seatingLogic.conference.getTables()) {
                if(!table.assignedSeats.contains(draggedPerson)) {
                    if (table.bounds.overlaps(draggedPerson.bounds)) {
                        seatingLogic.setOverTable(true, this.table, draggedPerson);
                        foundTable = true;
                    }
                }
            }
            if(!foundTable) {
                seatingLogic.setOverTable(false, null, null);
            }
        } else if(draggedTable != null) {
            seatingLogic.moveTableToPosition(draggedTable, vec2);
        } else {
            Vector3 vec = camera.unproject(new Vector3(0, 0, 0))
                    .add(camera.unproject(new Vector3(deltaX, deltaY, 0)).scl(-1f));

            camera.position.add(vec);
            camera.update();
        }

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if(draggedPerson != null) {
            for(Table table : seatingLogic.conference.getTables()) {
                if(!table.assignedSeats.contains(draggedPerson)) {
                    if (table.bounds.overlaps(draggedPerson.bounds)) {
                        seatingLogic.addPersonToTable(table, draggedPerson);
                        break;
                    }
                }
            }

            for(Table table : seatingLogic.conference.getTables()) {
                if(table.assignedSeats.contains(draggedPerson)) {
                    seatingLogic.resetTable(table);
                    break;
                }
            }

            draggedPerson = null;
        }  else if(draggedTable != null) {
            State.save();
            draggedTable = null;

        } else {
            float xx = MathUtils.clamp(camera.position.x, 0, (float) seatingLogic.conference.conferenceWidth);
            float yy = MathUtils.clamp(camera.position.y, 0, (float) seatingLogic.conference.conferenceHeight);

            if (xx != camera.position.x || yy != camera.position.y) {
                Tween.to(camera, CameraAccessor.POSITION_XY, .3f).target(xx, yy)
                        .start(TweenUtil.getTweenManager());
            }
        }

        seatingLogic.setOverTable(false, null, null);

        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    private Vector2 oldInitialFirstPointer=null, oldInitialSecondPointer=null;
    private float oldScale;
    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
        draggedTable = null;
        draggedPerson = null;

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
        ((OrthographicCamera)camera).zoom = Math.min(2.0f, Math.max(((OrthographicCamera)camera).zoom, 0.2f));
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

    /**
     * Converts world to screen coords
     *
     * @param x
     * @param y
     * @return
     */
    private Vector3 worldToScreenCoords(float x, float y) {
        Vector3 worldPos = new Vector3(x, y, 0);
        camera.project(worldPos);

        return worldPos;
    }
}
