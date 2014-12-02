package se.tribestar.mobil14.lab2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onStartClick(View view) {
		Intent intent = new Intent(MainActivity.this, NMMActivity.class);
		startActivity(intent);
	}

	public void onExitClick(View view) {
		finish();
	}

}