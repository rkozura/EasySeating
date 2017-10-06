package com.kozu.easyseating.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/5/2017.
 */

public class PhotoCarousel implements Disposable {
    private List<TextureRegion> photos;

    public PhotoCarousel() {
        photos = new ArrayList<>();
    }

    public void addPhoto(FileHandle fileHandle){
        photos.add(new TextureRegion(new Texture(fileHandle)));
    }

    @Override
    public void dispose() {
        for(TextureRegion region : photos) {
            region.getTexture().dispose();
        }
    }
}
