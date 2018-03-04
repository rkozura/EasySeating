package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.Assets;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.controller.SeatingController;
import com.kozu.easyseating.logic.SeatingLogic;
import com.kozu.easyseating.logic.State;
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

    @LmlActor("personFirstName") private VisTextField personFirstName;
    @LmlActor("personLastName") private VisTextField personLastName;

    @LmlActor("editPersonFirstName") private VisTextField editPersonFirstName;
    @LmlActor("editPersonLastName") private VisTextField editPersonLastName;

    @LmlActor("venueListView") private ListView.ListViewTable venueListView;
    @LmlActor("contactsListView") private ListView.ListViewTable contactsListView;

    @LmlActor("deleteTableButton") private TextButton deleteTableButton;
    @LmlActor("doneMovingTableButton") private TextButton doneMovingTableButton;

    @LmlActor("openVenueButton") private TextButton openVenueButton;
    @LmlActor("openOptionsButton") private TextButton openOptionsButton;

    @LmlActor("addPersonToTableButton") private TextButton addPersonToTableButton;
    @LmlActor("doneEditingTableButton") private TextButton doneEditingTableButton;

    @LmlActor("addRemoveTablesTable") private com.badlogic.gdx.scenes.scene2d.ui.Table addRemoveTablesTable;

    @LmlActor("toggleAddRemoveButton") private TextButton toggleAddRemoveButton;
    @LmlActor("toggleAddRemoveLabel") private Label toggleAddRemoveLabel;

    @LmlActor("tableSplitPane") private VisSplitPane tableSplitPane;

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

    public static Table getEditTable() {
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
        venuePeopleListAdapter.itemsChanged();
        venueDialog.getTitleLabel().setText(seatingLogic.conference.conferenceName);
        venueDialog.setVisible(true);
        venueDialog.show(getStage());
    }

    private static Table selectedTable;
    public void openTable(Table table) {
        selectedTable = table;

        tableList.clear();
        editTableAddPeopleList.clear();
        editTableRemovePeopleList.clear();
        peopleWithoutTable.clear();

        peopleWithoutTable.addAll(seatingLogic.conference.persons);
        peopleWithoutTable.removeAll(selectedTable.assignedSeats);

        tableList.addAll(table.assignedSeats);

        tablePeopleListAdapter.itemsChanged();
        venuePersonTableAdapter.itemsChanged();

        tableDialog.getTitleLabel().setText("Table "+table.tableIdentifier);
        tableDialog.setVisible(true);
        tableDialog.show(getStage());
        tableDialog.toFront();
    }

    @LmlAction("export")
    public void export(VisTextButton visTextButton) {
        System.out.println("export");
    }

    private boolean shouldResize = true;
    @LmlAction("openCreatePersonDialog")
    public void openCreatePersonDialog() {
        shouldResize = false;
        EasySeatingGame.importer.checkPermission();
        createPersonDialog.setVisible(true);
        createPersonDialog.show(getStage());
        createPersonDialog.toFront();
    }

    @LmlAction("openCustomPersonDialog")
    public void createPerson() {
        personFirstName.setText("");
        personLastName.setText("");

        createPersonDialog.hide();

        customPersonDialog.setVisible(true);
        customPersonDialog.show(getStage());
        customPersonDialog.toFront();

        customPersonDialog.setPosition(customPersonDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        //The following three lines are absolutely needed for correct input on mobile
//        FocusManager.switchFocus(getStage(), personFirstName);
//        getStage().setKeyboardFocus(personFirstName);
//        Gdx.input.setOnscreenKeyboardVisible(true);
    }

    @LmlAction("createCustomPerson")
    public void createCustomPerson() {
        if(StringUtils.isBlank(personFirstName.getText()) || StringUtils.isBlank(personLastName.getText())) {
            final ToastManager manager = getToastManager(getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Invalid Person Name", 1.5f);
            manager.toFront();
        } else {
            boolean isDuplicate = false;
            for(Person person : seatingLogic.conference.persons) {
                if(person.getFirstName().equalsIgnoreCase(personFirstName.getText()) && person.getLastName().equalsIgnoreCase(personLastName.getText())) {
                    isDuplicate = true;
                    break;
                }
            }
            if(isDuplicate) {
                final ToastManager manager = getToastManager(getStage());
                manager.clear();
                manager.setAlignment(Align.topLeft);
                manager.show("Person already exists", 1.5f);
                manager.toFront();
            } else {
                seatingLogic.createPerson(personFirstName.getText(), personLastName.getText());

                venuePeopleListAdapter.itemsChanged();
                venuePersonTableAdapter.itemsChanged();

                customPersonDialog.hide();
            }
        }
    }

    private Person selectedPerson;
    @LmlAction("venuePersonListener")
    public void venuePersonListener(final Person selectedItem) {
        selectedPerson = selectedItem;

        editPersonFirstName.setText(selectedItem.getFirstName());
        editPersonLastName.setText(selectedItem.getLastName());

        editPersonDialog.getTitleLabel().setText("Person Details");
        editPersonDialog.setVisible(true);
        editPersonDialog.show(getStage());
        editPersonDialog.toFront();
        editPersonDialog.setPosition(editPersonDialog.getX(), Gdx.graphics.getHeight()-(Gdx.graphics.getPpiX()/2));

        //The following three lines are absolutely needed for correct input on mobile
        FocusManager.switchFocus(getStage(), editPersonFirstName);
        getStage().setKeyboardFocus(editPersonFirstName);
        Gdx.input.setOnscreenKeyboardVisible(true);
        editPersonFirstName.setCursorAtTextEnd();
    }

    @LmlAction("tableVenuePersonListener")
    public void tableVenuePersonListener(final Person selectedItem) {
        tableList.add(selectedItem);

        peopleWithoutTable.remove(selectedItem);

        editTableAddPeopleList.add(selectedItem);

        VisTable view = tablePeopleListAdapter.getView(selectedItem);
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pm1.setColor(new Color(.514f, .988f, .616f, .2f));
        pm1.fill();

        view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

        //seatingLogic.addPersonToTable(selectedTable, selectedItem);

        tablePeopleListAdapter.itemsDataChanged();
        venuePersonTableAdapter.itemsChanged();
    }

    Array<Person> editTableRemovePeopleList = new Array<Person>();
    Array<Person> editTableAddPeopleList = new Array<Person>();
    @LmlAction("confirmEditTable")
    public void confirmEditTable() {
        Table table = getEditTable();
        for(Person person : editTableRemovePeopleList) {
            seatingLogic.removePersonFromTable(table, person);
            tableList.remove(person);
            peopleWithoutTable.add(person);
        }
        for(Person person : editTableAddPeopleList) {
            seatingLogic.addPersonToTable(table, person);
        }

        editTableRemovePeopleList.clear();
        editTableAddPeopleList.clear();

        tablePeopleListAdapter.itemsChanged();
        venuePersonTableAdapter.itemsChanged();

        //tableDialog.hide();
    }

    @LmlAction("openConfirmDeletePersonDialog")
    public void openConfirmDeletePersonDialog() {
        Gdx.app.getInput().setOnscreenKeyboardVisible(false);
        confirmDeletePersonDialog.setVisible(true);
        confirmDeletePersonDialog.show(getStage());
        confirmDeletePersonDialog.toFront();
    }

    @LmlAction("deletePerson")
    public void deletePerson() {
        seatingLogic.removePerson(selectedPerson);
        venuePeopleListAdapter.itemsChanged();
        tablePeopleListAdapter.itemsChanged();
        venuePersonTableAdapter.itemsChanged();
        editPersonDialog.hide();
        confirmDeletePersonDialog.hide();
        State.save();
    }

    @LmlAction("confirmEditPerson")
    public void confirmEditPerson() {
        if(editPersonFirstName.getText().isEmpty() && editPersonLastName.getText().isEmpty()) {
            ToastManager manager = getToastManager(getStage());
            manager.clear();
            manager.setAlignment(Align.topLeft);
            manager.show("Name can't be empty!", 1.5f);
            manager.toFront();

            FocusManager.switchFocus(getStage(), editPersonFirstName);
            getStage().setKeyboardFocus(editPersonFirstName);
            Gdx.input.setOnscreenKeyboardVisible(true);
            editPersonFirstName.setCursorAtTextEnd();
        } else {
            selectedPerson.setName(editPersonFirstName.getText(), editPersonLastName.getText());
            venuePeopleListAdapter.itemsChanged();
            tablePeopleListAdapter.itemsChanged();
            venuePersonTableAdapter.itemsChanged();

            editPersonDialog.hide();
        }
    }

    @LmlAction("saveContactsDialog")
    public void saveContactsDialog() {
        seatingLogic.conference.persons.addAll(selectedContacts);
        selectedContacts.clear();
        venuePeopleListAdapter.itemsChanged();
        tablePeopleListAdapter.itemsChanged();
        venuePersonTableAdapter.itemsChanged();

        importContactsDialog.hide();
    }

    @LmlAction("tablePersonListener")
    public void tablePersonListener(final Person selectedItem) {
        VisTable view = tablePeopleListAdapter.getView(selectedItem);

        if(editTableAddPeopleList.contains(selectedItem, true)){
            editTableAddPeopleList.removeValue(selectedItem, true);
            peopleWithoutTable.add(selectedItem);
            tableList.remove(selectedItem);

            tablePeopleListAdapter.itemsDataChanged();
            venuePersonTableAdapter.itemsDataChanged();
        } else {
            if (editTableRemovePeopleList.contains(selectedItem, true)) {
                editTableRemovePeopleList.removeValue(selectedItem, true);

                Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
                pm1.setColor(new Color(.694f, .714f, .718f, .2f));
                pm1.fill();

                view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
            } else {
                editTableRemovePeopleList.add(selectedItem);

                Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
                pm1.setColor(new Color(.929f, .306f, .306f, 1));
                pm1.fill();

                view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
            }
        }
    }

    ArrayList<Person> tableList = new ArrayList<Person>();
    //Create two adapters for each list view of people
    private PeopleListAdapter<Person> tablePeopleListAdapter;
    @LmlAction("tablePersonAdapter")
    public ListAdapter<?> tablePersonAdapter() {
        tablePeopleListAdapter = new PeopleListAdapter<Person>(tableList);
        return tablePeopleListAdapter;
    }

    private PeopleListAdapter<Person> venuePeopleListAdapter;
    @LmlAction("venuePersonAdapter")
    public ListAdapter<?> venuePersonAdapter() {
        venuePeopleListAdapter = new PeopleListAdapter<Person>(seatingLogic.conference.persons);
        return venuePeopleListAdapter;
    }

    private ArrayList<Person> peopleWithoutTable = new ArrayList<>();
    private PeopleListAdapter<Person> venuePersonTableAdapter;
    @LmlAction("venuePersonTableAdapter")
    public ListAdapter<?> venuePersonTableAdapter() {
        venuePersonTableAdapter = new PeopleListAdapter<Person>(peopleWithoutTable);
        return venuePersonTableAdapter;
    }

    private PeopleListAdapter<Person> contactsPeopleListAdapter;
    private ArrayList<Person> contacts = new ArrayList<>();
    @LmlAction("contactsPersonAdapter")
    public ListAdapter<?> contactsPersonAdapter() {
        contactsPeopleListAdapter = new PeopleListAdapter<Person>(contacts);
        return contactsPeopleListAdapter;
    }

    private ArrayList<Person> selectedContacts = new ArrayList<Person>();
    @LmlAction("contactsPersonListener")
    public void contactsPersonListener(final Person selectedItem) {
        VisTable view = contactsPeopleListAdapter.getView(selectedItem);

        if(!selectedContacts.contains(selectedItem)) {
            selectedContacts.add(selectedItem);

            Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
            pm1.setColor(new Color(0, .502f, .949f, 1));
            pm1.fill();

            view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        } else {
            Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
            pm1.setColor(new Color(.694f, .714f, .718f, .2f));
            pm1.fill();

            view.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

            selectedContacts.remove(selectedItem);
            contactsPeopleListAdapter.itemsDataChanged();
        }
    }

    @Override
    public void resize(int width, int height, boolean centerCamera) {
        if(shouldResize) {
            viewport.update(width, height);
            getStage().getViewport().update(width, height, true);

            venueDialog.getContentTable().getCell(venueListView).height(getDialogHeight(null));
            venuePeopleListAdapter.itemsChanged();

            tableDialog.getContentTable().getCell(tableSplitPane).height(getSplitDialogHeight(null));
            tablePeopleListAdapter.itemsChanged();
            venuePersonTableAdapter.itemsChanged();

            importContactsDialog.getContentTable().getCell(contactsListView).height(getDialogHeight(null));
            contactsPeopleListAdapter.itemsChanged();

            venueDialog.invalidateHierarchy();
            venueDialog.pack();
            tableDialog.invalidateHierarchy();
            tableDialog.pack();
            importContactsDialog.invalidate();
            importContactsDialog.pack();

            for (Actor actor : getStage().getActors()) {
                if (actor instanceof DialogSize) {
                    ((DialogSize) actor).hide();
                }
            }

            super.resize(width, height, centerCamera);
        }

        shouldResize = true;
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

    @LmlAction("dialogHeight")
    public float getDialogHeight(final VisTable container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX()*1.2f;
    }

    @LmlAction("splitDialogHeight")
    public float getSplitDialogHeight(final VisSplitPane container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX()*1.2f;
    }

    @LmlAction("tableDialogHeight")
    public float getTableDialogHeight(final VisTable container) {
        //Allow .5 inches of clickable space above and below the dialog
        return Gdx.graphics.getHeight()-Gdx.graphics.getPpiX()*1.2f/2;
    }

    @LmlAction("initTextField")
    public void initTextField(final VisTextField visTextField) {
        visTextField.setTextFieldFilter(new VisTextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(VisTextField textField, char c) {
                if(isAlpha(Character.toString(c))) {
                    if(textField.getText().length() >= 12) {
                        ToastManager manager = getToastManager(getStage());
                        manager.clear();
                        manager.setAlignment(Align.topLeft);
                        manager.show("Name too long!", 1.5f);
                        manager.toFront();

                        return false;
                    } else {
                        return true;
                    }
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
        ArrayList<Person> contactList = EasySeatingGame.importer.getPersonList();
        shouldResize = false;

        if(contactList != null) {
            shouldResize = true;
            contacts.clear();
            contacts.addAll(contactList);
            contacts.removeAll(seatingLogic.conference.persons);
            contactsPeopleListAdapter.itemsChanged();

            selectedContacts.clear();

            createPersonDialog.hide();

            importContactsDialog.setVisible(true);
            importContactsDialog.show(getStage());
            importContactsDialog.toFront();
        }
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

    public static boolean addRemoveTable;
    public static boolean removeTable;
    public void showAddRemoveTablesTable() {
        removeTable = false;
        addRemoveTable = true;
        toggleAddRemoveButton.setText("Remove");
        toggleAddRemoveLabel.setText("Tap to add table");
        addRemoveTablesTable.setVisible(true);
        openVenueButton.setVisible(false);
        openOptionsButton.setVisible(false);
    }

    @LmlAction("toggleAddRemove")
    public void toggleAddRemove() {
        removeTable = !removeTable;
        if(removeTable) {
            toggleAddRemoveButton.setText("Add");
            toggleAddRemoveLabel.setText("Tap to remove table");
        } else {
            toggleAddRemoveButton.setText("Remove");
            toggleAddRemoveLabel.setText("Tap to add table");
        }
    }

    @LmlAction("doneAddRemoveTablesTable")
    public void doneAddRemoveTablesTable() {
        addRemoveTable = false;
        addRemoveTablesTable.setVisible(false);
        openVenueButton.setVisible(true);
        openOptionsButton.setVisible(true);
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