package com.kozu.easyseating.tweenutil;

import com.badlogic.gdx.graphics.Color;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Rob on 12/21/2017.
 */

public class ColorAccessor implements TweenAccessor<Color> {
    public static final int ALPHA = 1;

    @Override
    public int getValues(Color target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA: returnValues[0] = target.a; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Color target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA: target.a = newValues[0]; break;
            default: assert false; break;
        }
    }
}