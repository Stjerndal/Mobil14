package se.tribestar.mobil14.sensorbloom;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class FlowerActivity extends Activity {

	private FlowerSurfaceView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get the dimensions of the display
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Create the view
		view = new FlowerSurfaceView(this, metrics.widthPixels, metrics.heightPixels);
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
