package com.kozu.easyseating.tweenutil;

import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Robert on 8/4/2017.
 */
public class TweenUtil {
    private static TweenManager tweenManager = new TweenManager();

    static {
        Tween.registerAccessor(Table.class, new EntityAccessor());
        Tween.registerAccessor(Person.class, new EntityAccessor());
        Tween.setCombinedAttributesLimit(4);
    }


    public synchronized static TweenManager getTweenManager() {
        return tweenManager;
    }
}