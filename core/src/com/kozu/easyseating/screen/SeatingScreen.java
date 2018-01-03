package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlView;
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
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.controller.SeatingController;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.object.Conference;
import com.kozu.easyseating.object.Person;
import com.kozu.easyseating.object.Table;
import com.kozu.easyseating.renderer.SeatingRenderer;
import com.kozu.easyseating.tweenutil.CameraAccessor;
import com.kozu.easyseating.tweenutil.ColorAccessor;
import com.kozu.easyseating.tweenutil.TweenUtil;
import com.kozu.easyseating.ui.DialogSize;
import com.kozu.easyseating.ui.PeopleListAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Tween;

import static com.kozu.easyseating.EasySeatingGame.batch;

public class SeatingScreen extends AbstractLmlView {
    private OrthographicCamera camera;
    private Viewport viewport;
    private SeatingRenderer renderer;
    private SeatingController gestureDetector;
    private SeatingLogic seatingLogic;

    private ToastManager toastManager;

    private Assets assets;

    @LmlActor("venueDialog") private DialogSize venueDialog;
    @LmlActor("tableDialog") private DialogSize tableDialog;
    @LmlActor("createPersonDialog") private DialogSize createPersonDialog;
    @LmlActor("customPersonDialog") private DialogSize customPersonDialog;
    @LmlActor("editPersonDialog") private DialogSize editPersonDialog;
    @LmlActor("confirmDeletePersonDialog") private DialogSize confirmDeletePersonDialog;
    @LmlActor("importContactsDialog") private DialogSize importContactsDialog;
    @LmlActor("createObjectDialog") private DialogSize createObjectDialog;

    @LmlActor("personName") private VisTextField personName;

    @LmlActor("venueListView") private ListView.ListViewTable venueListView;
    @LmlActor("tableListView") private ListView.ListViewTable tableListView;
    @LmlActor("contactsListView") private ListView.ListViewTable contactsListView;

    @LmlActor("deleteTableButton") private TextButton deleteTableButton;
    @LmlActor("doneMovingTableButton") private TextButton doneMovingTableButton;

    @LmlActor("openVenueButton") private TextButton openVenueButton;
    @LmlActor("openOptionsButton") private TextButton openOptionsButton;

    @LmlActor("addPersonToTableButton") private TextButton addPersonToTableButton;
    @LmlActor("doneEditingTableButton") private TextButton doneEditingTableButton;

    public SeatingScreen(Conference conference, Assets assets) {
        super(new Stage(new ScreenViewport()));

        seatingLogic = new SeatingLogic(conference);
        initConference(assets);
    }

    public SeatingScreen(String venueName, Assets assets) {
        super(new Stage(new ScreenViewport()));

        seatingLogic = new SeatingLogic(venueName);
        initConference(assets);
    }

    private void initConference(Assets assets) {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.zoom = 3f;

        //Setup the controller, which listens for gestures
        gestureDetector = new SeatingController(camera, seatingLogic, this);

        //Create the renderer.  Renderer needs to see the logic to know what to render
        renderer = new SeatingRenderer(seatingLogic, assets);

        this.assets = assets;
    }

    public void editTable(Table table) {
        Tween.to(seatingLogic.getBackgroundTint(), ColorAccessor.ALPHA, .3f).target(.20f)
                .start(TweenUtil.getTweenManager());

        selectedTable = table;
        addPersonToTableButton.setVisible(true);
        doneEditingTableButton.setVisible(true);
        openVenueButton.setVisible(false);
        openOptionsButton.setVisible(false);
    }

    public Table getEditTable() {
        return selectedTable;
    }

    @LmlAction("openAddPersonToTableDialog")
    public void openAddPersonToTableDialog() {
        seatingLogic.resetTable(selectedTable);
        gestureDetector.cancelDragPerson();
        openTable(selectedTable);
    }

    @LmlAction("doneEditingTable")
    public void doneEditingTable() {
        TweenUtil.getTweenManager().killTarget(camera);

        Table table = getEditTable();
        //Find all the people at the table that where marked to be removed and remove them
        //Have to use two collections or else concurrent modification error will happen
        List<Person> people = new ArrayList<Person>();
        for(Person person : table.assignedSeats) {
            if(person.isFlaggedForRemoval()) {
                people.add(person);
            }
        }
        for(Person person : people) {
            seatingLogic.removePersonFromTable(table, person);
        }

        seatingLogic.resetTable(selectedTable);
        gestureDetector.cancelDragPerson();
        selectedTable = null;
        addPersonToTableButton.setVisible(false);
        doneEditingTableButton.setVisible(false);
        openVenueButton.setVisible(true);
        openOptionsButton.setVisible(true);

        Tween.to(camera, CameraAccessor.ZOOM, .3f).target(1f) //TODO this is needed when killing a tween target, it somehow uses a bad pool entry and frees up good ones for the next two tween calls
                .start(TweenUtil.getTweenManager());
        Tween.to(camera, CameraAccessor.ZOOM, .3f).target(1f)
                .start(TweenUtil.getTweenManager());
        Tween.to(seatingLogic.getBackgroundTint(), ColorAccessor.ALPHA, .3f).target(1)
                .start(TweenUtil.getTweenManager());
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/SeatingView.lml");
    }

    @Override
    public String getViewId() {
        return "third";
    }

    @Override
    public void show() {
        //Center the camera to the center of the game world
        camera.position.set((float)seatingLogic.conference.conferenceWidth/2, (float)seatingLogic.conference.conferenceHeight/2f, 0);
        camera.update();

        //Zoom the camera in from high to low.  Gives the user an overview of the seating
        Tween.to(camera, CameraAccessor.ZOOM, 1.9f).target(1f)
                .start(TweenUtil.getTweenManager());

        GdxUtilities.setMultipleInputProcessors(Gdx.input.getInputProcessor(), gestureDetector);
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);

        //TODO Is this really the solution to pan the screen?
        gestureDetector.personPan();
        viewport.apply();
        renderer.render();
        getStage().getViewport().apply();
        super.render(delta);

    }

    @LmlAction("openOptions")
    public void openOptions() {
        EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
        LmlView optionsView = new OptionsDialogView(getStage(), seatingLogic);
        core.getParser().createView(optionsView, Gdx.files.internal("views/OptionsView.lml"));
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

        TextureRegion tr = new TextureRegion(assets.manager.get(Assets.tabletexture));
        TiledDrawable tableTile = new TiledDrawable(tr);

        //TODO BUG when method is called again, the background resizes the content table
        tableDialog.getContentTable().setBackground(tableTile);
        tableDialog.getContentTable().pack();

        setPersonSelection();
    }

    public static Table moveTable;
    public void enableMoveTable(Table table) {
        if (selectedTable == null) {
            moveTable = table;
            doneMovingTableButton.setVisible(true);
            deleteTableButton.setVisible(true);
            openVenueButton.setVisible(false);
            openOptionsButton.setVisible(false);
        }
    }

    @LmlAction("doneMovingTable")
    public void doneMovingTable(TextButton textButton) {
        moveTable = null;
        doneMovingTableButton.setVisible(false);
        deleteTableButton.setVisible(false);
        openVenueButton.setVisible(true);
        openOptionsButton.setVisible(true);
    }

    @LmlAction("deleteTable")
    public void deleteTable(TextButton textButton) {
        seatingLogic.removeTable(moveTable);
        //TODO add confirmation dialog before deleting!!!
        doneMovingTable(textButton);
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

    @LmlAction("saveContactsDialog")
    public void saveContactsDialog() {
        seatingLogic.conference.persons.addAll(selectedContacts);
        selectedContacts.clear();
        venuePeopleListAdapter.itemsChanged();
        tablePeopleListAdapter.itemsChanged();

        importContactsDialog.hide();
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

    private PeopleListAdapter<Person> contactsPeopleListAdapter;
    @LmlAction("contactsPersonAdapter")
    public ListAdapter<?> contactsPersonAdapter() {
        contactsPeopleListAdapter = new PeopleListAdapter<Person>(EasySeatingGame.importer.getPersonList());
        return contactsPeopleListAdapter;
    }

    private Array<Person> selectedContacts = new Array<Person>();
    @LmlAction("contactsPersonListener")
    public void contactsPersonListener(final Person selectedItem) {
        System.out.println(selectedItem);
        VisTable view = contactsPeopleListAdapter.getView(selectedItem);

        if(!selectedContacts.contains(selectedItem, true)) {
            selectedContacts.add(selectedItem);
            System.out.println(view.getChildren().get(0));
            view.getChildren().get(0).setColor(Color.CYAN);
        } else {
            selectedContacts.removeValue(selectedItem, true);
            view.getChildren().get(0).setColor(VisUI.getSkin().get(Label.LabelStyle.class).fontColor);
        }
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
        viewport.update(width, height);
        getStage().getViewport().update(width, height, true);

        venueDialog.getContentTable().getCell(venueListView).height(getDialogHeight(null));
        venuePeopleListAdapter.itemsChanged();

        tableDialog.getContentTable().getCell(tableListView).height(getDialogHeight(null));
        tablePeopleListAdapter.itemsChanged();

        importContactsDialog.getContentTable().getCell(contactsListView).height(getDialogHeight(null));
        contactsPeopleListAdapter.itemsChanged();

        venueDialog.invalidateHierarchy();
        venueDialog.pack();
        tableDialog.invalidateHierarchy();
        tableDialog.pack();
        importContactsDialog.invalidate();
        importContactsDialog.pack();

        centerActorOnStage(venueDialog);
        centerActorOnStage(tableDialog);
        centerActorOnStage(createPersonDialog);
        centerActorOnStage(customPersonDialog);
        centerActorOnStage(customPersonDialog);
        centerActorOnStage(editPersonDialog);
        centerActorOnStage(confirmDeletePersonDialog);
        centerActorOnStage(importContactsDialog);

        super.resize(width, height, centerCamera);
    }

    private void centerActorOnStage(Actor actor) {
        actor.setPosition(Math.round((getStage().getWidth() - actor.getWidth()) / 2),
                Math.round((getStage().getHeight() - actor.getHeight()) / 2));
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

    @LmlAction("openImportContactsDialog")
    public void openImportContactsDialog() {
        //clear the selected contacts in case user hid the dialog
        selectedContacts.clear();

        Array<Person> contactPeople = EasySeatingGame.importer.getPersonList();
        Array<Person> conferencePeople = seatingLogic.conference.persons;

        //Only show contacts that have not been added to the conference
        contactPeople.removeAll(conferencePeople, false);

        //Clear and retrieve the person list in case contacts were added
        contactsPeopleListAdapter.clear();
        contactsPeopleListAdapter.addAll(contactPeople);
        createPersonDialog.hide();

        importContactsDialog.setVisible(true);
        importContactsDialog.show(getStage());
        importContactsDialog.toFront();
    }

    private Vector3 longPressLocation;
    public void openCreateObjectDialog(Vector3 longPressLocation) {
        if (selectedTable == null) {
            this.longPressLocation = longPressLocation;

            createObjectDialog.setVisible(true);
            createObjectDialog.show(getStage());
            createObjectDialog.toFront();
        }
    }

    @LmlAction("createRoundTable")
    public void createRoundTable() {
        seatingLogic.addTableAtPosition(longPressLocation);
        createObjectDialog.hide();
    }

    @LmlAction("createObject")
    public void createObject() {
        //TODO Create round table
        System.out.println("Creating object");
    }

    @LmlAction("createSquareTable")
    public void createSquareTable() {
        //TODO Create square table
        System.out.println("Creating square table");
    }

    public boolean isAlpha(String s){
        String pattern= "^[a-zA-Z ]*$";
        return s.matches(pattern);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        super.dispose();
    }
}