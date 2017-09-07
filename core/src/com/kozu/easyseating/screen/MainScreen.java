package com.kozu.easyseating.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlInject;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kozu.easyseating.ui.DialogSize;

import org.apache.commons.lang3.StringUtils;

import static com.kozu.easyseating.EasySeatingGame.uiSkin;

/**
 * Created by Rob on 8/27/2017.
 */

public class MainScreen extends AbstractLmlView {
    private ToastManager toastManager;
    @LmlInject
    private LmlParser parser;

    public MainScreen() {
        super(new Stage());
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("views/MainMenuView.lml");
    }

    @Override
    public String getViewId() {
        return "second";
    }

    @LmlAction("onErrorApprove")
    public boolean onErrorApprove(final DialogSize dialog) {
        String venueName = ((VisTextField) LmlUtilities.getActorWithId(dialog, "venueName")).getText();
        System.out.println("a"+venueName);
        if(StringUtils.isBlank(venueName)) {
            final ToastManager manager = getToastManager();
            manager.show("Wrong Venue Name");
            manager.toFront();
            return ReflectedLmlDialog.CANCEL_HIDING;
//            EasySeatingGame core = (EasySeatingGame) Gdx.app.getApplicationListener();
//            core.getParser().fillStage(getStage(), Gdx.files.internal("views/NewVenueDialog.lml"));
        } else {
return true;
        }
    }

    @LmlAction("setup")
    public String setup(final VisTextField textField) {
        getStage().setKeyboardFocus(textField);
        getStage().addActor(new TextButton("",uiSkin));
        return "";
    }

    private ToastManager getToastManager() {
        if (toastManager == null) {
            toastManager = new ToastManager(getStage());
        }
        return toastManager;
    }
}
