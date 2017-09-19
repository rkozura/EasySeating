package com.kozu.easyseating.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kozu.easyseating.EasySeatingGame;
import com.kozu.easyseating.desktop.resolver.DesktopPersonImporter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 1; //Samples adds anti-aliasing, needed for round tables
        config.width = 800;
        config.height = 480;
		new LwjglApplication(new EasySeatingGame(new DesktopPersonImporter()), config);
	}
}
