package com.kozu.easyseating.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kozu.easyseating.EasySeatingGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 2; //Samples adds anti-aliasing
		new LwjglApplication(new EasySeatingGame(), config);
	}
}
