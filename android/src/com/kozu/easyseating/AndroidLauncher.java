package com.kozu.easyseating;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.kozu.easyseating.resolver.AndroidPDFGenerator;
import com.kozu.easyseating.resolver.AndroidPersonImporter;

public class AndroidLauncher extends AndroidApplication {
	public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4;

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
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
        switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    log("GRANTED","GRANTED");
                    // permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {
                    log("GRANTED","GRANTED");

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
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
