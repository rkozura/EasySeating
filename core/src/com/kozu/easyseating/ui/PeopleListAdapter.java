package com.kozu.easyseating.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.object.Person;

import java.util.Comparator;

/**
 * Created by Rob on 9/9/2017.
 */

public class PeopleListAdapter<ItemT> extends ArrayAdapter<ItemT, VisTable> {
    private SimpleListAdapter.SimpleListAdapterStyle style;

    private float maxTableWidth = 300;

    public PeopleListAdapter(Array<ItemT> array) {
        this(array, "default");
    }

    public PeopleListAdapter(Array<ItemT> array, String styleName) {
        this(array, VisUI.getSkin().get(styleName, SimpleListAdapter.SimpleListAdapterStyle.class));
    }

    public PeopleListAdapter(Array<ItemT> array, SimpleListAdapter.SimpleListAdapterStyle style) {
        super(array);
        this.style = style;

        setItemsSorter(new Comparator<ItemT>() {
            @Override
            public int compare(ItemT itemT, ItemT t1) {
                Person personOne = (Person)itemT;
                Person personTwo = (Person)t1;

                int returnValue = personOne.getName().compareTo(personTwo.getName());
                VisTable personOneTable = getView(itemT);
                VisTable personTwoTable = getView(t1);

                if((personOneTable.getChildren().get(0)).getColor().equals(Color.CYAN) && !(personTwoTable.getChildren().get(0)).getColor().equals(Color.CYAN)){
                    returnValue = -1;
                } else if(!(personOneTable.getChildren().get(0)).getColor().equals(Color.CYAN) && (personTwoTable.getChildren().get(0)).getColor().equals(Color.CYAN)) {
                    returnValue = 1;
                }

                return returnValue;
            }
        });
    }

    @Override
    protected VisTable createView(ItemT item) {
        Person person = (Person)item;
        VisLabel tableLabel = new VisLabel();
        VisLabel personLabel = new VisLabel(person.getName());
        personLabel.setWrap(true);

        VisTable table = new VisTable();
        table.add(personLabel).growX();
        table.add(tableLabel).padLeft(10f);

        return table;
    }
}
