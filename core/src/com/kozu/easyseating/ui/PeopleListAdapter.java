package com.kozu.easyseating.ui;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kozu.easyseating.object.Person;

/**
 * Created by Rob on 9/9/2017.
 */

public class PeopleListAdapter<ItemT> extends ArrayAdapter<ItemT, VisTable> {
    private SimpleListAdapter.SimpleListAdapterStyle style;

    public PeopleListAdapter(Array<ItemT> array) {
        this(array, "default");
    }

    public PeopleListAdapter(Array<ItemT> array, String styleName) {
        this(array, VisUI.getSkin().get(styleName, SimpleListAdapter.SimpleListAdapterStyle.class));
    }

    public PeopleListAdapter(Array<ItemT> array, SimpleListAdapter.SimpleListAdapterStyle style) {
        super(array);
        this.style = style;
    }

    @Override
    protected VisTable createView(ItemT item) {
        Person person = (Person)item;
        VisLabel visLabel = new VisLabel(person.getName());

        VisTable table = new VisTable();
        table.add(visLabel).growX();
        return table;
    }
}
