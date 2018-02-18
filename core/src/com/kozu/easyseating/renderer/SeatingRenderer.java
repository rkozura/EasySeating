package com.kozu.easyseating.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.screen.SeatingScreen;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingRenderer implements Disposable{
    private TiledDrawable tableTile;
    private TiledDrawable floorTile;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private SeatingLogic seatingLogic;

    //Positions the Bitmapfonts
    private static GlyphLayout glyphLayout = new GlyphLayout();

    private Assets assets;

    public SeatingRenderer(SeatingLogic seatingLogic, Assets assets) {
        tableTile = new TiledDrawable(new TextureRegion(assets.manager.get(assets.tabletexture)));
        floorTile = new TiledDrawable(new TextureRegion(assets.manager.get(assets.floortexture)));

        this.seatingLogic = seatingLogic;
        this.assets = assets;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    public void render() {
        shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());

        renderFloor();
        renderTables();
    }

    public void renderTables() {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        if(seatingLogic.conference.getTables().size() > 0) {
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glColorMask(false, false, false, false);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Table table : seatingLogic.conference.getTables()) {
                shapeRenderer.circle(table.bounds.x, table.bounds.y, table.bounds.radius);
            }
            shapeRenderer.end();

            //Draw the table texture
            EasySeatingGame.batch.begin();
            Gdx.gl.glColorMask(true, true, true, true);
            Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
            tableTile.draw(EasySeatingGame.batch, 0, 0, (float)seatingLogic.conference.conferenceWidth,
                    (float)seatingLogic.conference.conferenceHeight);
            EasySeatingGame.batch.end();

            //Disable depth testing so people are not clipped
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (Table table : seatingLogic.conference.getTables()) {
                shapeRenderer.circle(table.bounds.x, table.bounds.y, table.bounds.radius);
            }
            shapeRenderer.end();

            //Draw the table identifier text
            EasySeatingGame.batch.begin();
            for (Table table : seatingLogic.conference.getTables()) {
                assets.manager.get(Assets.buttontext).setColor(Color.BLACK);
                glyphLayout.setText(assets.manager.get(Assets.buttontext), table.tableIdentifier);
                //Now draw the table identifier
                assets.manager.get(Assets.buttontext).draw(EasySeatingGame.batch, glyphLayout,
                        table.bounds.x - glyphLayout.width/2, table.bounds.y + glyphLayout.height/2);
            }
            EasySeatingGame.batch.end();

            for (Table table : seatingLogic.conference.getTables()) {
                renderAssignedSeats(table);
            }
        }
    }

    public void renderAssignedSeats(Table table) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Person person : table.assignedSeats) {
            if (!person.isFlaggedForRemoval()) {
                shapeRenderer.setColor(Color.CYAN);
            } else {
                shapeRenderer.setColor(Color.RED);
            }
            //If projection matrices are not set, objects will not be drawn relative to the tables
            shapeRenderer.circle(person.bounds.x, person.bounds.y, person.bounds.radius);
        }
        shapeRenderer.end();
        EasySeatingGame.batch.begin();
        for(Person person : table.assignedSeats) {
            //Draw the person text
            if(seatingLogic.getBackgroundTint().a == 1) {
                assets.manager.get(Assets.persontext).setColor(Color.BLACK);
            } else {
                assets.manager.get(Assets.persontext).setColor(Color.WHITE);
            }
            glyphLayout.setText(assets.manager.get(Assets.persontext), person.getTruncatedName());
            assets.manager.get(Assets.persontext).draw(EasySeatingGame.batch, glyphLayout,
                    person.bounds.x - glyphLayout.width/2, person.bounds.y + glyphLayout.height/2);
        }
        EasySeatingGame.batch.end();

        if(seatingLogic.isOverTable()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GOLD);
            float tableX = seatingLogic.table.getX();
            float tableY = seatingLogic.table.getY();
            float personX = seatingLogic.person.getX();
            float personY = seatingLogic.person.getY();
            shapeRenderer.rectLine(tableX, tableY, personX, personY, 20);
            shapeRenderer.end();
        }
    }

    private void renderFloor() {
        EasySeatingGame.batch.begin();
        //Set a tint based on if a table is being viewed or not
        EasySeatingGame.batch.setColor(seatingLogic.getBackgroundTint());
        floorTile.draw(EasySeatingGame.batch, 0, 0, (float)seatingLogic.conference.conferenceWidth,
                (float)seatingLogic.conference.conferenceHeight);
        EasySeatingGame.batch.setColor(1, 1, 1, 1);
        EasySeatingGame.batch.end();

        float gridAlpha;
        if(SeatingScreen.addRemoveTable) {
            gridAlpha = 1f;
        } else {
            gridAlpha = .1f;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.getColor().a = gridAlpha;

        float currentX = 0;
        for (double i = 0; i <= seatingLogic.conference.gridCountWidth; i++) {
            shapeRenderer.line(currentX, 0, currentX, (float) seatingLogic.conference.conferenceHeight);
            currentX += seatingLogic.conference.gridGutterLength;
        }
        float currentY = 0;
        for (double i = 0; i <= seatingLogic.conference.gridCountHeight; i++) {
            shapeRenderer.line(0, currentY, (float) seatingLogic.conference.conferenceWidth, currentY);
            currentY += seatingLogic.conference.gridGutterLength;
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if(seatingLogic.conference.getTables().isEmpty()) {
            EasySeatingGame.batch.begin();
            glyphLayout.setText(assets.manager.get(Assets.buttontext), "Tap and hold to add tables");
            assets.manager.get(Assets.buttontext).setColor(Color.BLACK);
            assets.manager.get(Assets.buttontext).draw(EasySeatingGame.batch, glyphLayout,
                    (float)((seatingLogic.conference.conferenceWidth/2f) - glyphLayout.width / 2f),
                    (float)((seatingLogic.conference.conferenceHeight/2f) + glyphLayout.height / 2f));
            EasySeatingGame.batch.end();
        }
    }
}
