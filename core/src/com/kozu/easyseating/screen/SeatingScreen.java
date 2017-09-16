package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.controller.SeatingController;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.renderer.SeatingRenderer;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;
import com.kozu.easyseating.ui.PeopleListAdapter;

import org.apache.commons.lang3.StringUtils;

import aurelienribon.tweenengine.Tween;

import static com.kozu.easyseating.EasySeatingGame.batch;

public class SeatingScreen extends AbstractLmlView {
    private OrthographicCamera camera;
    private SeatingRenderer renderer;
    private GestureDetector gestureDetector;
    private SeatingLogic seatingLogic;

    private ToastManager toastManager;
    @LmlActor("optionsDialog") private DialogSize optionsDialog;
    @LmlActor("venueDialog") private DialogSize venueDialog;
    @LmlActor("tableDialog") private DialogSize tableDialog;
    @LmlActor("createPersonDialog") private DialogSize createPersonDialog;
    @LmlActor("customPersonDialog") private DialogSize customPersonDialog;
    @LmlActor("editPersonDialog") private DialogSize editPersonDialog;
    @LmlActor("confirmDeletePersonDialog") private DialogSize confirmDeletePersonDialog;

    @LmlActor("personName") private VisTextField personName;

    @LmlActor("venueListView") private ListView.ListViewTable venueListView;
    @LmlActor("tableListView") private ListView.ListViewTable tableListView;

    public SeatingScreen() {
        super(new Stage(new ScreenViewport()));
        //Create the camera and apply a viewport
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        viewport = new ScreenViewport(camera);
//        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        camera.unproject(new Vector3(0, 0, 0));
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/SeatingView.lml");
    }

    @Override
    public String getViewId() {
        return "third";
    }

    public void setConferenceName(String conferenceName) {
        //Create the logic class...has methods to modify objects and return them
        seatingLogic = new SeatingLogic(conferenceName);

        //Setup the controller, which listens for gestures
        gestureDetector = new GestureDetector(new SeatingController(camera, seatingLogic, this));

        //Create the renderer.  Renderer needs to see the logic to know what to render
        renderer = new SeatingRenderer(seatingLogic);
    }

    @Override
    public void show() {
        //Zoom the camera in from high to low.  Gives the user an overview of the seating
        camera.zoom = 3f;
        Tween.to(camera, CameraAccessor.ZOOM, 1.9f).target(1f)
                .start(TweenUtil.getTweenManager());

        GdxUtilities.setMultipleInputProcessors(Gdx.input.getInputProcessor(), gestureDetector);
    }

    @Override
    public void render(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        renderer.render();
        super.render(delta);

    }

    @LmlAction("openOptions")
    public void openOptions() {
        optionsDialog.setVisible(true);
        optionsDialog.show(getStage());
    }

    @LmlAction("openVenue")
    public void openVenue() {
        venueDialog.getTitleLabel().setText(seatingLogic.conference.conferenceName);
        venueDialog.setVisible(true);
        venueDialog.show(getStage());
    }

    private Table selectedTable;
    public void openTable(Table table) {
        selectedTable = table;
        tableDialog.getTitleLabel().setText("Table "+table.tableIdentifier);
        tableDialog.setVisible(true);
        tableDialog.show(getStage());
        tableDialog.toFront();

        setPersonSelection();
    }

    @LmlAction("export")
    public void export(VisTextButton visTextButton) {
        System.out.println("export");
    }

    @LmlAction("openCreatePersonDialog")
    public void openCreatePersonDialog() {
        createPersonDialog.setVisible(true);
        createPersonDialog.show(getStage());
        createPersonDialog.toFront();
    }

    @LmlAction("openCustomPersonDialog")
    public void createPerson() {
        personName.setText("");

        createPersonDialog.hide();

        customPersonDialog.setVisible(true);
        customPersonDialog.show(getStage());
        customPersonDialog.toFront();

        customPersonDialog.setPosition(customPersonDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        getStage().setKeyboardFocus(personName);
        Gdx.input.setOnscreenKeyboardVisible(true);
    }

    @LmlAction("createCustomPerson")
    public void createCustomPerson() {
        String personName = ((VisTextField) LmlUtilities.getActorWithId(customPersonDialog, "personName")).getText();
        if(StringUtils.isBlank(personName)) {
            final ToastManager manager = getToastManager(getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Invalid Person Name", 1.5f);
            manager.toFront();
        } else {
            seatingLogic.createPerson(personName);

            venuePeopleListAdapter.itemsChanged();
            tablePeopleListAdapter.itemsChanged();

            setPersonSelection();

            customPersonDialog.hide();
        }
    }

    private Person selectedPerson;
    @LmlAction("venuePersonListener")
    public void venuePersonListener(final Person selectedItem) {
        selectedPerson = selectedItem;

        VisTextField renamePersonTextField = (VisTextField)LmlUtilities.getActorWithId(editPersonDialog, "editPersonName");
        renamePersonTextField.setText(selectedItem.getName());

        editPersonDialog.getTitleLabel().setText("Person Details");
        editPersonDialog.setVisible(true);
        editPersonDialog.show(getStage());
        editPersonDialog.toFront();
    }

    @LmlAction("openConfirmDeletePersonDialog")
    public void openConfirmDeletePersonDialog() {
        confirmDeletePersonDialog.setVisible(true);
        confirmDeletePersonDialog.show(getStage());
        confirmDeletePersonDialog.toFront();
    }

    @LmlAction("deletePerson")
    public void deletePerson() {
        seatingLogic.removePerson(selectedPerson);
        venuePeopleListAdapter.itemsChanged();
        tablePeopleListAdapter.itemsChanged();
        editPersonDialog.hide();
        confirmDeletePersonDialog.hide();
    }

    @LmlAction("confirmEditPerson")
    public void confirmEditPerson() {
        VisTextField renamePersonTextField = (VisTextField)LmlUtilities.getActorWithId(editPersonDialog, "editPersonName");
        if(!renamePersonTextField.getText().equals(selectedPerson.getName())) {
            selectedPerson.setName(renamePersonTextField.getText());
            venuePeopleListAdapter.itemsChanged();
            tablePeopleListAdapter.itemsChanged();
        }
        editPersonDialog.hide();
    }

    @LmlAction("tablePersonListener")
    public void tablePersonListener(final Person selectedItem) {
        VisTable view = tablePeopleListAdapter.getView(selectedItem);

        if(seatingLogic.isPersonAtTable(selectedTable, selectedItem)) {
            seatingLogic.removePersonFromTable(selectedTable, selectedItem);
            view.getChildren().get(0).setColor(VisUI.getSkin().get(Label.LabelStyle.class).fontColor);
        } else {
            seatingLogic.addPersonToTable(selectedTable, selectedItem);
            view.getChildren().get(0).setColor(Color.CYAN);
            ((VisLabel)view.getChildren().get(1)).setText("");
        }
    }

    //Create two adapters for each list view of people
    private PeopleListAdapter<Person> tablePeopleListAdapter;
    @LmlAction("tablePersonAdapter")
    public ListAdapter<?> tablePersonAdapter() {
        tablePeopleListAdapter = new PeopleListAdapter<Person>(seatingLogic.conference.persons);
        return tablePeopleListAdapter;
    }

    private PeopleListAdapter<Person> venuePeopleListAdapter;
    @LmlAction("venuePersonAdapter")
    public ListAdapter<?> venuePersonAdapter() {
        venuePeopleListAdapter = new PeopleListAdapter<Person>(seatingLogic.conference.persons);
        return venuePeopleListAdapter;
    }

    /**
     * Sets the person selection
     */
    private void setPersonSelection() {
        if(selectedTable != null) {
            for (Person person : tablePeopleListAdapter.iterable()) {
                VisTable view = tablePeopleListAdapter.getView(person);
                if (seatingLogic.isPersonAtTable(selectedTable, person)) {
                    (view.getChildren().get(0)).setColor(Color.CYAN);
                    ((VisLabel)view.getChildren().get(1)).setText("");
                } else {
                    (view.getChildren().get(0)).setColor(VisUI.getSkin().get(Label.LabelStyle.class).fontColor);
                    Table assignedTable = seatingLogic.getAssignedTable(person);
                    if(assignedTable != null) {
                        ((VisLabel) view.getChildren().get(1)).setText(assignedTable.tableIdentifier);
                    }
                }
            }
        }

        //This will resort the array based on what is selected
        tablePeopleListAdapter.itemsDataChanged();
    }

    @Override
    public void resize(int width, int height, boolean centerCamera) {
        getStage().getViewport().setWorldSize(width, height);
        getStage().getViewport().update(width, height, true);

        venueDialog.getContentTable().getCell(venueListView).height(getDialogHeight(null));
        venuePeopleListAdapter.itemsChanged();

        tableDialog.getContentTable().getCell(tableListView).height(getDialogHeight(null));
        tablePeopleListAdapter.itemsChanged();

        venueDialog.invalidateHierarchy();
        venueDialog.pack();
        tableDialog.invalidateHierarchy();
        tableDialog.pack();

        centerActorOnStage(optionsDialog);
        centerActorOnStage(venueDialog);
        centerActorOnStage(tableDialog);
        centerActorOnStage(createPersonDialog);
        centerActorOnStage(customPersonDialog);
        centerActorOnStage(customPersonDialog);
        centerActorOnStage(editPersonDialog);
        centerActorOnStage(confirmDeletePersonDialog);

        super.resize(width, height, centerCamera);
    }

    private void centerActorOnStage(Actor actor) {
        actor.setPosition(Math.round((getStage().getWidth() - actor.getWidth()) / 2),
                Math.round((getStage().getHeight() - actor.getHeight()) / 2));;
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }

    @LmlAction("ppi")
    public float getPPI(final VisTable container) {
        return Gdx.graphics.getPpiX();
    }

    @LmlAction("dialogHeight")
    public float getDialogHeight(final VisTable container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX();
    }

    @LmlAction("initTextField")
    public void initTextField(final VisTextField visTextField) {
        visTextField.setTextFieldFilter(new VisTextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(VisTextField textField, char c) {
                if(isAlpha(Character.toString(c))) {
                    return true;
                } else {
                    ToastManager manager = getToastManager(getStage());
                    manager.clear();
                    manager.setAlignment(Align.topLeft);
                    manager.show("Invalid character!", 1.5f);
                    manager.toFront();

                    return false;
                }
            }
        });
    }

    //TODO add space as also acceptable
    public boolean isAlpha(String s){
        String pattern= "^[a-zA-Z]*$";
        return s.matches(pattern);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        super.dispose();
    }
}