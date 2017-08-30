package com.kozu.easyseating.tweenutil;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Robert on 8/4/2017.
 */
public class TweenUtil {
    private static TweenManager tweenManager = new TweenManager();

    public synchronized static TweenManager getTweenManager() {
        return tweenManager;
    }
}