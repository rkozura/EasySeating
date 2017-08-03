package com.kozu.easyseating.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kozu.easyseating.EasySeatingGame;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table {
    private int TABLE_RADIUS = 75;

    private static String vertexShader;
    private static String fragmentShader;
    private static ShaderProgram shaderProgram;

    private Circle bounds;
    private TiledDrawable tableTile;

    static {
        vertexShader = Gdx.files.internal("vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("fragment.glsl").readString();

        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        if (!shaderProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
    }

    public Table() {
        bounds = new Circle();
        bounds.set(100, 100, TABLE_RADIUS);

        Texture tableTexture = new Texture(Gdx.files.internal("lightpaperfibers.png"));
        TextureRegion tr = new TextureRegion(tableTexture);
        tableTile = new TiledDrawable(tr);
    }

    public void render() {
        //TODO Shaders probably arent being used correctly...fix
        shaderProgram.begin();
        int a = shaderProgram.getUniformLocation("center");
        shaderProgram.setUniformf(a, bounds.x, bounds.y);

        int b = shaderProgram.getUniformLocation("radius");
        shaderProgram.setUniformf(b, bounds.radius);
        shaderProgram.end();

        EasySeatingGame.batch.setShader(shaderProgram);

        //TODO remove this hardcoded value!
        //tableTile.draw(batch, 0, 0, 3181.5f, 2250);
        EasySeatingGame.batch.begin();
        tableTile.draw(EasySeatingGame.batch,  bounds.x - bounds.radius, bounds.y - bounds.radius, 1000, 1000);
        EasySeatingGame.batch.end();

        EasySeatingGame.batch.setShader(null);
    }
}