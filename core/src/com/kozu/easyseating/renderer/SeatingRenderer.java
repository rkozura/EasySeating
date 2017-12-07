package com.kozu.easyseating.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

import static com.kozu.easyseating.EasySeatingGame.uiSkin;

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

    //TODO move to assett manager
    Texture tableTexture = new Texture(Gdx.files.internal("images/game/lightpaperfibers.png"));

    //Texture floorTexture = new Texture(Gdx.files.internal("images/game/washi.png"));
    Texture floorTexture = new Texture(Gdx.files.internal("images/game/floor.jpg"));

    public SeatingRenderer(SeatingLogic seatingLogic) {
        tableTile = new TiledDrawable(new TextureRegion(tableTexture));
        floorTile = new TiledDrawable(new TextureRegion(floorTexture));

        this.seatingLogic = seatingLogic;
    }

    @Override
    public void dispose() {
        tableTexture.dispose();
        floorTexture.dispose();
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
                uiSkin.getFont("largetext").setColor(Color.BLACK);
                glyphLayout.setText(uiSkin.getFont("largetext"), table.tableIdentifier);
                //Now draw the table identifier
                uiSkin.getFont("largetext").draw(EasySeatingGame.batch, glyphLayout,
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
        shapeRenderer.setColor(Color.CYAN);
        for(Person person : table.assignedSeats) {
            //If projection matrices are not set, objects will not be drawn relative to the tables
            shapeRenderer.circle(person.bounds.x, person.bounds.y, person.bounds.radius);
        }
        shapeRenderer.end();
        EasySeatingGame.batch.begin();
        for(Person person : table.assignedSeats) {
            glyphLayout.setText(uiSkin.getFont("smalltext"), person.getTruncatedName());
            uiSkin.getFont("smalltext").setColor(Color.BLACK);
            //Now draw the table identifier
            uiSkin.getFont("smalltext").draw(EasySeatingGame.batch, glyphLayout,
                    person.bounds.x - glyphLayout.width/2, person.bounds.y + glyphLayout.height/2);
        }
        EasySeatingGame.batch.end();
    }

    private void renderFloor() {
        EasySeatingGame.batch.begin();
        floorTile.draw(EasySeatingGame.batch, 0, 0, (float)seatingLogic.conference.conferenceWidth,
                (float)seatingLogic.conference.conferenceHeight);
        EasySeatingGame.batch.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(0, 0, (float)seatingLogic.conference.conferenceWidth, (float)seatingLogic.conference.conferenceHeight);
//        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);

        float currentX = 0;
        for(double i=0; i<=seatingLogic.conference.gridCountWidth; i++) {
            shapeRenderer.line(currentX, 0, currentX, (float)seatingLogic.conference.conferenceHeight);
            currentX+=seatingLogic.conference.gridGutterLength;
        }

        float currentY = 0;
        for(double i=0; i<=seatingLogic.conference.gridCountHeight; i++) {
            shapeRenderer.line(0, currentY, (float)seatingLogic.conference.conferenceWidth, currentY);
            currentY+=seatingLogic.conference.gridGutterLength;
        }

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(0, 0, (float)seatingLogic.conference.conferenceWidth, (float)seatingLogic.conference.conferenceHeight);

        shapeRenderer.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
//        shapeRenderer.setColor(Color.RED);
//        for(Vector2 point : seatingLogic.conference.snapGrid.keySet()) {
//            shapeRenderer.point(point.x, point.y, 0);
//        }
//        shapeRenderer.end();
    }
}
