package com.kozu.easyseating.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

/**
 * Created by Rob on 8/2/2017.
 */

public class SeatingRenderer {
    private Table table;

    private TiledDrawable tableTile;
    private static String vertexShader;
    private static String fragmentShader;
    private static ShaderProgram shaderProgram;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    static {
        vertexShader = Gdx.files.internal("vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("fragment.glsl").readString();

        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        if (!shaderProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
    }

    public SeatingRenderer(Table table) {
        this.table = table;

        Texture tableTexture = new Texture(Gdx.files.internal("lightpaperfibers.png"));
        TextureRegion tr = new TextureRegion(tableTexture);
        tableTile = new TiledDrawable(tr);
    }

    public void render() {
        renderTables();
    }

    public void renderTables() {
        //TODO Shaders probably arent being used correctly...fix
        //Assign variables in the shader program
        shaderProgram.begin();
        int a = shaderProgram.getUniformLocation("center");
        shaderProgram.setUniformf(a, table.bounds.x, table.bounds.y);

        int b = shaderProgram.getUniformLocation("radius");
        shaderProgram.setUniformf(b, table.bounds.radius);
        shaderProgram.end();

        EasySeatingGame.batch.setShader(shaderProgram);
        EasySeatingGame.batch.begin();
        tableTile.draw(EasySeatingGame.batch,  table.bounds.x - table.bounds.radius, table.bounds.y - table.bounds.radius, 1000, 1000);
        EasySeatingGame.batch.end();
        EasySeatingGame.batch.setShader(null);

        renderAssignedSeats(table);

    }

    public void renderAssignedSeats(Table table) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //TODO this probably needs to be in some sort of logic class
        double nextSeatAngle = 0;
        for(Person person : table.assignedSeats) {
            double nextSeatRadians = Math.toRadians(nextSeatAngle);
            float x = (float) (table.bounds.radius * Math.cos(nextSeatRadians)) + table.bounds.radius;
            float y = (float) (table.bounds.radius * Math.sin(nextSeatRadians)) + table.bounds.radius;

            //If projection matrices are not set, objects will not be drawn relative to the tables
            shapeRenderer.setProjectionMatrix(EasySeatingGame.batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(EasySeatingGame.batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.circle(x, y, 30);
            shapeRenderer.end();


            nextSeatAngle += 30;
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
