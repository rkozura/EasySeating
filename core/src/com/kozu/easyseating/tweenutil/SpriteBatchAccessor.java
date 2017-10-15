package com.kozu.easyseating.tweenutil;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Christen on 10/15/2017.
 */

public class SpriteBatchAccessor implements TweenAccessor<Batch> {
    public static final int ALPHA = 1;

    @Override
    public int getValues(Batch target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA: returnValues[0] = target.getColor().a; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Batch target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA: target.getColor().a = newValues[0]; break;
            default: assert false; break;
        }
    }
}
