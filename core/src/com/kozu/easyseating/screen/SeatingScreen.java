package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;
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


    @LmlActor("peopleVenuePane") private ScrollPane peopleVenuePane;

    public SeatingScreen() {
        super(new Stage());
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
        VisTextField customPersonTextField = (VisTextField)LmlUtilities.getActorWithId(customPersonDialog, "personName");
        customPersonTextField.setText("");
        //getStage().setKeyboardFocus(customPersonTextField); //TODO Text field not focusing

        createPersonDialog.hide();

        customPersonDialog.setVisible(true);
        customPersonDialog.show(getStage());
        customPersonDialog.toFront();
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

            customPersonDialog.hide();
        }
    }

    private Person selectedPerson;
    @LmlAction("venuePersonListener")
    public void venuePersonListener(final Person selectedItem) {
        selectedPerson = selectedItem;

        VisTextField renamePersonTextField = (VisTextField)LmlUtilities.getActorWithId(editPersonDialog, "editPersonName");
        renamePersonTextField.setText(selectedItem.getName());

        editPersonDialog.getTitleLabel().setText(selectedItem.getName()+" Details");
        editPersonDialog.setVisible(true);
        editPersonDialog.show(getStage());
        editPersonDialog.toFront();
    }

    @LmlAction("deletePerson")
    public void deletePerson() {
        seatingLogic.removePerson(selectedPerson);
        venuePeopleListAdapter.itemsChanged();
        tablePeopleListAdapter.itemsChanged();
        editPersonDialog.hide();
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
        if(seatingLogic.isPersonAtTable(selectedTable, selectedItem)) {
            seatingLogic.removePersonFromTable(selectedTable, selectedItem);
        } else {
            seatingLogic.addPersonToTable(selectedTable, selectedItem);
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

    @Override
    public void resize(int width, int height) {
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
        super.resize(width, height);
    }

    private ToastManager getToastManager(Stage stage) {
        if (toastManager == null) {
            toastManager = new ToastManager(stage);
        }
        return toastManager;
    }
}