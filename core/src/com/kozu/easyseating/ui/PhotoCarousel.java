package com.kozu.easyseating.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kozu.easyseating.EasySeatingGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/5/2017.
 */

public class PhotoCarousel implements Disposable {
    private List<TextureRegion> photos;
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

        for (FileHandle entry: dirHandle.list()) {
            addPhoto(entry);
        }

        viewport.setWorldSize(getCurrentTextureRegion().getRegionWidth(), getCurrentTextureRegion().getRegionHeight());

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
                //Resize the viewport so the new texture fits on the screen
                viewport.setWorldSize(getCurrentTextureRegion().getRegionWidth(), getCurrentTextureRegion().getRegionHeight());
                int width = Gdx.graphics.getWidth();
                int height = Gdx.graphics.getHeight();
                viewport.getCamera().position.set(width/2f, height/2f, 0);
                viewport.update(width, height);

//                Tween.to(viewport.getCamera(), CameraAccessor.POSITION_XY, 20f)
//                        .target(viewport.getCamera().position.x-100, viewport.getCamera().position.y-100)
//                        .repeatYoyo(-1, 2)
//                        .start(TweenUtil.getTweenManager());
            }
        };
        timer.scheduleTask(task, 10, 5);
        timer.start();
    }

    public void addPhoto(FileHandle fileHandle){
        Texture texture = new Texture(fileHandle);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        photos.add(new TextureRegion(texture));
    }

    public void draw () {
        if(previousIndex != -1) {
            EasySeatingGame.batch.draw(getPreviousTextureRegion(), 0, 0);
        }
        EasySeatingGame.batch.draw(getCurrentTextureRegion(), 0, 0);
    }

    public TextureRegion getPreviousTextureRegion() {
        return photos.get(previousIndex);
    }

    public TextureRegion getCurrentTextureRegion() {
        return photos.get(currentIndex);
    }

    @Override
    public void dispose() {
        for(TextureRegion region : photos) {
            region.getTexture().dispose();
        }
    }
}