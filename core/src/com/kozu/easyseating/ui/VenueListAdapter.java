package com.kozu.easyseating.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter;
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.object.Person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Rob on 9/9/2017.
 */

public class VenueListAdapter<ItemT> extends ArrayListAdapter<ItemT, VisTable> {
    private SimpleListAdapter.SimpleListAdapterStyle style;

    private float nameLabelSize;

    public VenueListAdapter(ArrayList<ItemT> array) {
        this(array, "default");
    }

    public VenueListAdapter(ArrayList<ItemT> array, String styleName) {
        this(array, VisUI.getSkin().get(styleName, SimpleListAdapter.SimpleListAdapterStyle.class), -1);
    }

    public VenueListAdapter(ArrayList<ItemT> array, SimpleListAdapter.SimpleListAdapterStyle style, float nameLabelSize) {
        super(array);
        this.style = style;

        setItemsSorter(new Comparator<ItemT>() {
            @Override
            public int compare(ItemT itemT, ItemT t1) {
                String keyOne = (String)itemT;
                String keyTwo = (String)t1;

                int returnValue = keyOne.compareTo(keyTwo);

                return returnValue;
            }
        });

        this.nameLabelSize = nameLabelSize;
    }

    @Override
    protected VisTable createView(ItemT item) {
        String entry = (String)item;

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.BLACK);
        pm1.fill();

        VisLabel venueLabel = new VisLabel(entry);

        VisTable table = new VisTable();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        table.add(venueLabel).growX().height(.25f* Gdx.graphics.getPpiX());

        return table;
    }

}
