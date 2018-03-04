package com.kozu.easyseating.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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

import static com.kozu.easyseating.EasySeatingGame.batch;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingRenderer implements Disposable{
    private Color tableTrimColor = new Color(.047f, .561f, .8f, 1f);
    private Color personColor = new Color(.667f, .937f, 1f, 1f);

    private TiledDrawable tableTile;
    private TiledDrawable floorTile;
    private TiledDrawable floorBackgroundTile;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private SeatingLogic seatingLogic;

    //Positions the Bitmapfonts
    private static GlyphLayout glyphLayout = new GlyphLayout();

    private Assets assets;

    private Camera uiCamera;
    private Camera gameCamera;

    public SeatingRenderer(SeatingLogic seatingLogic, Assets assets, Camera uiCamera, Camera gameCamera) {
        tableTile = new TiledDrawable(new TextureRegion(assets.manager.get(assets.tabletexture)));
        floorTile = new TiledDrawable(new TextureRegion(assets.manager.get(assets.floortexture)));
        floorBackgroundTile = new TiledDrawable(new TextureRegion(assets.manager.get(assets.venueBackgroundTexture)));

        this.seatingLogic = seatingLogic;
        this.assets = assets;
        this.uiCamera = uiCamera;
        this.gameCamera = gameCamera;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    public void render() {
        batch.setProjectionMatrix(uiCamera.combined);
        renderBackground();

        batch.setProjectionMatrix(gameCamera.combined);
        shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());

        renderFloor();
        renderTables();
    }

    public void renderTables() {
        for (int i = 0; i < seatingLogic.conference.getTables().size(); i++) {
            Table table = seatingLogic.conference.getTables().get(i);
            renderAssignedSeats(table);
        }

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        if(seatingLogic.conference.getTables().size() > 0) {
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glColorMask(false, false, false, false);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < seatingLogic.conference.getTables().size(); i++) {
                Table table = seatingLogic.conference.getTables().get(i);
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

            //Draw a black outline around the table
            Gdx.gl.glLineWidth(6);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < seatingLogic.conference.getTables().size(); i++) {
                Table table = seatingLogic.conference.getTables().get(i);
                if(SeatingScreen.getEditTable() == table) {
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(table.bounds.x, table.bounds.y, table.bounds.radius);
                } else {
                    shapeRenderer.setColor(tableTrimColor);
                    shapeRenderer.circle(table.bounds.x, table.bounds.y, table.bounds.radius);
                }
            }
            shapeRenderer.end();
            Gdx.gl.glLineWidth(1);

            //Draw the table identifier text
            EasySeatingGame.batch.begin();
            for (int i = 0; i < seatingLogic.conference.getTables().size(); i++) {
                Table table = seatingLogic.conference.getTables().get(i);
                assets.manager.get(Assets.buttontext).setColor(Color.BLACK);
                glyphLayout.setText(assets.manager.get(Assets.buttontext), table.tableIdentifier);
                //Now draw the table identifier
                assets.manager.get(Assets.buttontext).draw(EasySeatingGame.batch, glyphLayout,
                        table.bounds.x - glyphLayout.width/2, table.bounds.y + glyphLayout.height/2);

                renderAssignedSeatsText(table);
            }
            EasySeatingGame.batch.end();
        }
    }

    public void renderAssignedSeatsText(Table table) {
        for(int i = 0; i < table.assignedSeats.size(); i++) {
            Person person = table.assignedSeats.get(i);
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
    }

    public void renderAssignedSeats(Table table) {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < table.assignedSeats.size(); i++) {
            Person person = table.assignedSeats.get(i);
            if (!person.isFlaggedForRemoval()) {
                shapeRenderer.setColor(personColor);
            } else {
                shapeRenderer.setColor(Color.RED);
            }
            //If projection matrices are not set, objects will not be drawn relative to the tables
            shapeRenderer.circle(person.bounds.x, person.bounds.y, person.bounds.radius);

            //Draw a line from the person to the middle of the table
            shapeRenderer.rectLine(person.bounds.x, person.bounds.y, table.getX(), table.getY(), 10);
        }
        shapeRenderer.end();
        //Gdx.gl.glDisable(GL20.GL_BLEND);

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

    private void renderBackground() {
        EasySeatingGame.batch.begin();
        floorBackgroundTile.draw(EasySeatingGame.batch, 0, 0, (float)seatingLogic.conference.conferenceWidth,
                (float)seatingLogic.conference.conferenceHeight);
        EasySeatingGame.batch.end();
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

        if(seatingLogic.conference.getTables().isEmpty() && !SeatingScreen.addRemoveTable) {
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
