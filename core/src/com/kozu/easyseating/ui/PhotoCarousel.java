package com.kozu.easyseating.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.tweenutil.SpriteAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Tween;

/**
 * Created by Rob on 10/5/2017.
 */

public class PhotoCarousel implements Disposable {
    private List<Sprite> photos;
    private int currentIndex = 0;
    private int previousIndex = -1;

    public PhotoCarousel(final Viewport viewport) {
        photos = new ArrayList<>();

        FileHandle dirHandle;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            dirHandle = Gdx.files.internal("images/backgrounds");
        } else {
            //TODO May have to change path for desktop
            // ApplicationType.Desktop ..
            dirHandle = Gdx.files.internal("images/backgrounds");
        }

        for (FileHandle entry : dirHandle.list()) {
            addPhoto(entry);
        }

        //Find the smallest photo and set the world size to it
        Sprite smallestSprite = null;
        int smallestArea = 0;
        for(Sprite photo : photos) {
            if(smallestSprite == null) {
                smallestSprite = photo;
                smallestArea = photo.getRegionHeight() * photo.getRegionWidth();
            } else {
                int thisArea = photo.getRegionHeight() * photo.getRegionWidth();
                if(thisArea < smallestArea) {
                    smallestArea = thisArea;
                    smallestSprite = photo;
                }
            }
        }
        viewport.setWorldSize(smallestSprite.getRegionWidth(), smallestSprite.getRegionHeight());

        Timer timer = new Timer();
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                if(currentIndex == photos.size()-1) {
                    previousIndex = currentIndex;
                    currentIndex = 0;

                } else {
                    previousIndex = currentIndex;
                    currentIndex ++;
                }
                getCurrentTextureRegion().setAlpha(0);
                Tween.to(getCurrentTextureRegion(), SpriteAccessor.ALPHA, 3f)
                        .target(1f)
                        .start(TweenUtil.getTweenManager());
            }
        };
        timer.scheduleTask(task, 10, 15);
        timer.start();
    }

    public void addPhoto(FileHandle fileHandle){
        Texture texture = new Texture(fileHandle);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Sprite sprite = new Sprite(texture);

        photos.add(sprite);
    }

    public void draw () {
        if(previousIndex != -1) {
            getPreviousTextureRegion().draw(EasySeatingGame.batch);
        }

        getCurrentTextureRegion().draw(EasySeatingGame.batch);
    }

    public Sprite getPreviousTextureRegion() {
        return photos.get(previousIndex);
    }

    public Sprite getCurrentTextureRegion() {
        return photos.get(currentIndex);
    }

    @Override
    public void dispose() {
        for(TextureRegion region : photos) {
            region.getTexture().dispose();
        }
    }
}
