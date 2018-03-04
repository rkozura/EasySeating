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
    private ArrayList<Comparator<ItemT>> comparators = new ArrayList<Comparator<ItemT>>();
    private int comparatorIndex = 0;

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

        comparators.add(new FirstNameComparator<ItemT>());
        comparators.add(new LastNameComparator<ItemT>());
        comparators.add(new TableComparator<ItemT>());

        setItemsSorter(comparators.get(comparatorIndex));

        this.nameLabelSize = nameLabelSize;
    }

    public void changeComparator() {
        comparatorIndex++;

        if(comparatorIndex >= comparators.size()) {
            comparatorIndex = 0;
        }

        setItemsSorter(comparators.get(comparatorIndex));

        itemsChanged();
    }

    public void defaultComparator() {
        comparatorIndex = 0;

        setItemsSorter(comparators.get(comparatorIndex));

        itemsChanged();
    }

    public String getCurrentComparatorName() {
        if(comparators.get(comparatorIndex) instanceof LastNameComparator) {
            return "Last Name";
        }

        if(comparators.get(comparatorIndex) instanceof FirstNameComparator) {
            return "First Name";
        }

        return "Table";
    }

    @Override
    protected VisTable createView(ItemT item) {
        Person person = (Person)item;

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pm1.setColor(new Color(.694f, .714f, .718f, .2f));
        pm1.fill();

        VisLabel tableLabel = new VisLabel(person.assignedTable);

        VisLabel personLabel;
        if(person.getFirstName().isEmpty() && !person.getLastName().isEmpty()) {
            personLabel = new VisLabel(person.getLastName());
        } else if(!person.getFirstName().isEmpty() && person.getLastName().isEmpty()) {
            personLabel = new VisLabel(person.getFirstName());
        } else {
            personLabel = new VisLabel( person.getFirstName() + " " + person.getLastName());
        }

        VisScrollPane personScroll = new VisScrollPane(personLabel);
        personScroll.setScrollingDisabled(true, false);

        VisTable table = new VisTable();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        table.add(tableLabel).center().width(100);
        table.add(personScroll).growX().height(.25f* Gdx.graphics.getPpiX()).padLeft(10f);

        return table;
    }

    class LastNameComparator<ItemT> implements Comparator<ItemT> {
        @Override
        public int compare(ItemT itemT, ItemT t1) {
            Person personOne = (Person)itemT;
            Person personTwo = (Person)t1;

            int returnValue = (personOne.getLastName()+personOne.getFirstName()).compareTo(personTwo.getLastName()+personTwo.getFirstName());

            return returnValue;
        }
    }

    class FirstNameComparator<ItemT> implements Comparator<ItemT> {
        @Override
        public int compare(ItemT itemT, ItemT t1) {
            Person personOne = (Person)itemT;
            Person personTwo = (Person)t1;

            int returnValue = (personOne.getFirstName()+personOne.getLastName()).compareTo(personTwo.getFirstName()+personTwo.getLastName());

            return returnValue;
        }
    }

    class TableComparator<ItemT> implements Comparator<ItemT> {
        @Override
        public int compare(ItemT itemT, ItemT t1) {
            Person personOne = (Person)itemT;
            Person personTwo = (Person)t1;

            int returnValue = (personOne.assignedTable+personOne.getFirstName()+personOne.getLastName()).compareTo(personTwo.assignedTable+personTwo.getFirstName()+personTwo.getLastName());

            return returnValue;
        }
    }

}
