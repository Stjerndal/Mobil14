package se.tribestar.mobil14.lab2;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class NMMActivity extends Activity {

	private NMMView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the dimensions of the display
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Create the view
		view = new NMMView(this, metrics.widthPixels, metrics.heightPixels);
		this.setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.pause();
	}
}