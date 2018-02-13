package com.kozu.easyseating;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.kozu.easyseating.resolver.AndroidPDFGenerator;
import com.kozu.easyseating.resolver.AndroidPersonImporter;

public class AndroidLauncher extends AndroidApplication {
    AndroidPersonImporter importer;
    AndroidPDFGenerator pdfGenerator;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        importer = new AndroidPersonImporter(this, getContext());
        pdfGenerator = new AndroidPDFGenerator(getContext());
		// Create the layout
		RelativeLayout layout = new RelativeLayout(this);

		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		// Create the libgdx View
		EasySeatingGame seatingChart = new EasySeatingGame(importer, pdfGenerator);
		View gameView = initializeForView(seatingChart);

		// Add the libgdx view
		layout.addView(gameView);

		// Hook it all up
		setContentView(layout);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
