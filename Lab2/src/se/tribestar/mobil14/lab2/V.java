package se.tribestar.mobil14.lab2;

import android.util.Log;

//Small helper class
public class V {
	public final static boolean DEBUG = true;
	public final static String TAG = "LAB2";

	// Simplified log method.
	public static void log(String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

	/**
	 * Convert i from logical index to weird NMMRules index.
	 */
	public static int convert(int i) {
		switch (i) {
		case 0:
			return 3;
		case 1:
			return 6;
		case 2:
			return 9;
		case 3:
			return 2;
		case 4:
			return 5;
		case 5:
			return 8;
		case 6:
			return 1;
		case 7:
			return 4;
		case 8:
			return 7;
		case 9:
			return 24;
		case 10:
			return 23;
		case 11:
			return 22;
		case 12:
			return 10;
		case 13:
			return 11;
		case 14:
			return 12;
		case 15:
			return 19;
		case 16:
			return 16;
		case 17:
			return 13;
		case 18:
			return 20;
		case 19:
			return 17;
		case 20:
			return 14;
		case 21:
			return 21;
		case 22:
			return 18;
		case 23:
			return 15;
		}

		return -1;
	}
}
