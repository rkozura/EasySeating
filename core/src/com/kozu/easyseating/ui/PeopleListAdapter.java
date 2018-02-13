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

/**
 * Created by Rob on 9/9/2017.
 */

public class PeopleListAdapter<ItemT> extends ArrayListAdapter<ItemT, VisTable> {
    private SimpleListAdapter.SimpleListAdapterStyle style;

    private float nameLabelSize;

    public PeopleListAdapter(ArrayList<ItemT> array) {
        this(array, "default");
    }

    public PeopleListAdapter(ArrayList<ItemT> array, String styleName) {
        this(array, VisUI.getSkin().get(styleName, SimpleListAdapter.SimpleListAdapterStyle.class), -1);
    }

    public PeopleListAdapter(ArrayList<ItemT> array, SimpleListAdapter.SimpleListAdapterStyle style, float nameLabelSize) {
        super(array);
        this.style = style;

        setItemsSorter(new Comparator<ItemT>() {
            @Override
            public int compare(ItemT itemT, ItemT t1) {
                Person personOne = (Person)itemT;
                Person personTwo = (Person)t1;

                int returnValue = personOne.getName().compareTo(personTwo.getName());

                return returnValue;
            }
        });

        this.nameLabelSize = nameLabelSize;
    }

    @Override
    protected VisTable createView(ItemT item) {
        Person person = (Person)item;

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.PINK);
        pm1.fill();

        VisLabel tableLabel = new VisLabel(person.assignedTable);
        VisLabel personLabel = new VisLabel(person.getName());
        VisScrollPane personScroll = new VisScrollPane(personLabel);
        personScroll.setScrollingDisabled(true, false);
        //personLabel.setWrap(true);

        VisTable table = new VisTable();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        table.add(tableLabel).width(30);
        table.add(personScroll).growX().height(.25f* Gdx.graphics.getPpiX()).padLeft(10f);

        return table;
    }

}
