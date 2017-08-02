package com.kozu.easyseating.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Rob on 8/1/2017.
 */

public class Table {
    private int TABLE_RADIUS = 75;

    private static String vertexShader;
    private static String fragmentShader;
    private static ShaderProgram shaderProgram;

    private int 

    static {
        vertexShader = Gdx.files.internal("vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("fragment.glsl").readString();

        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        if (!shaderProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
    }

    public Table() {
    }

    public void render() {
        //TODO Shaders probably arent being used correctly...fix
        shaderProgram.begin();
        int a = shaderProgram.getUniformLocation("center");
        shaderProgram.setUniformf(a, getX() + (getWidth() / 2), getY() + (getHeight() / 2));

        int b = shaderProgram.getUniformLocation("radius");
        shaderProgram.setUniformf(b, getWidth() / 2);
        shaderProgram.end();
        batch.setShader(shaderProgram);

        //TODO remove this hardcoded value!
        //tableTile.draw(batch, 0, 0, 3181.5f, 2250);
        tableTile.draw(batch, getX(), getY(), getWidth(), getHeight());

        batch.setShader(null);

        font.draw(batch, Integer.toString(getZIndex()), getX() + getWidth() / 2 - layout.width / 2, getY() + getHeight() / 2 + layout.height / 2);

        font.draw(batch, tableName, getX() + getWidth() / 2 - tableNameLayout.width / 2, getY() - 50);
    }
}