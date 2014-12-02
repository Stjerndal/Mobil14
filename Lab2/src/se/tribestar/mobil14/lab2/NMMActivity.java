package se.tribestar.mobil14.lab2;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			restartGame();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void restartGame() {
		view.restart();
	}

}