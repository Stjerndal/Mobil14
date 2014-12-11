package se.tribestar.mobil14.sensorbloom;

import android.util.Log;

//Small helper class
public class V {
	public final static boolean DEBUG = true;
	public final static String TAG = "LAB3A";

	// Simplified log method.
	public static void log(String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

}
