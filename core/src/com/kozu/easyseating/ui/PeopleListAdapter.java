package com.kozu.easyseating.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

        setSelectionMode(SelectionMode.MULTIPLE);
    }

    //TODO might delete.  Select and deselect view are on mouse down, and itemselection is on mouse up,
    //TODO would have to fix Vis code, setselectionmode needs to be checked as well
    private VisTable selectedView;
    @Override
    protected void selectView(VisTable view) {
        if(selectedView == null || selectedView != view) {
            (view.getChildren().get(0)).setColor(Color.CYAN);
            selectedView = view;
        } else {
            ((VisLabel)(view.getChildren().get(0))).setColor(VisUI.getSkin().get(Label.LabelStyle.class).fontColor);
            selectedView = null;
        }


    }

    @Override
    protected void deselectView(VisTable view) {
        //((VisLabel)(view.getChildren().get(0))).setColor(VisUI.getSkin().get(Label.LabelStyle.class).fontColor);

    }


    @Override
    public void itemsChanged() {

        super.itemsChanged();
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
