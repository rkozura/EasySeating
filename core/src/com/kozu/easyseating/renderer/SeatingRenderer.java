package com.kozu.easyseating.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingRenderer {
    public List<Table> tables = new ArrayList<Table>();

    private TiledDrawable tableTile;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    SeatingLogic seatingLogic;

    public SeatingRenderer(SeatingLogic seatingLogic) {
        //TODO move to assett manager
        Texture tableTexture = new Texture(Gdx.files.internal("lightpaperfibers.png"));
        TextureRegion tr = new TextureRegion(tableTexture);
        tableTile = new TiledDrawable(tr);

        this.seatingLogic = seatingLogic;
    }

    public void render() {
        renderTables();
        renderFloor();
    }

    public void renderTables() {
        for(Table table : seatingLogic.conference.getTables()) {
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glColorMask(false, false, false, false);

            shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(table.bounds.x, table.bounds.y, table.bounds.radius);
            shapeRenderer.end();

            EasySeatingGame.batch.begin();

            Gdx.gl.glColorMask(true, true, true, true);
            Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

            //Draw the table texture just enough to cover the table
            //TODO move the width/height computations to table class so it does not happen
            //every render
            tableTile.draw(EasySeatingGame.batch, table.bounds.x - table.bounds.radius,
                    table.bounds.y - table.bounds.radius, table.bounds.radius*2,
                    table.bounds.radius*2);

            EasySeatingGame.batch.end();

            //Disable depth testing so people are not clipped
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        for(Table table : seatingLogic.conference.getTables()) {
            renderAssignedSeats(table);
        }
    }

    public void renderAssignedSeats(Table table) {
        for(Person person : table.assignedSeats) {
            //If projection matrices are not set, objects will not be drawn relative to the tables
            shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.circle(person.position.x, person.position.y, 30);
            shapeRenderer.end();
        }
    }

    private void renderFloor() {
        shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(0, 0, seatingLogic.conference.conferenceWidth, seatingLogic.conference.conferenceHeight);
        shapeRenderer.end();

        for(Vector2 point : seatingLogic.conference.snapGrid.keySet()) {
            shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.point(point.x, point.y, 0);
            shapeRenderer.end();
        }
    }
}
