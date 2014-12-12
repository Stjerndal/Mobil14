package com.tribestar.bluetooth;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Thread bluetoothReceiver;
	// private PollDataTask pollData;
	private Thread serverSender;
	private String filename = "SensorData.txt";
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice noninDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// get the adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			showToast("This device do not support Bluetooth");
			this.finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onToggleClicked(View view) {
		V.log("CLICK");
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			// If the toggle is on we want to find the paired device
			Set<BluetoothDevice> pairedBTDevices = bluetoothAdapter.getBondedDevices();
			if (pairedBTDevices.size() > 0) {
				V.log("PAIRED WITH DEVICE");
				for (BluetoothDevice device : pairedBTDevices) {
					V.log("LOOP");
					String name = device.getName();
					if (name.contains("Nonin")) {
						noninDevice = device;
						showToast("Paired device: " + name);
						break;
						// return;
					}
				}
				V.log("POST LOOP");
			}
			// start a thread that will collect data from bluetooth and save to
			// file
			bluetoothReceiver = new PollDataTask(this, noninDevice, filename);
			bluetoothReceiver.start();
			showToast("Now downloading");

		} else {
			// If the toggle is not on, see if we can close the thread that is
			// receiving data
			// call cancel method to close all streams
			((PollDataTask) bluetoothReceiver).cancel();
			// call interrupt just in case
			bluetoothReceiver.interrupt();
			showToast("Stopped downloading");
		}
	}

	public void onSendDataToServerClicked(View view) {
		try {
			// if we clicked this we want to make sure that the file has been
			// finished writing to, so we call cancel on the bluetooth-thread
			// and interrupt just in case
			((PollDataTask) bluetoothReceiver).cancel();
			bluetoothReceiver.interrupt();

		} catch (Exception e) {

		}
		showToast("Contacting server");
		// start a new thread that will send the file to the server
		serverSender = new ServerSender(filename, this);
		serverSender.start();

	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	protected void onDestroy() {
		try {
			// make sure that the bluetooth-thread is shut down when we quit
			((PollDataTask) bluetoothReceiver).cancel();
			bluetoothReceiver.interrupt();
			// if (pollData != null) {
			// pollData.cancel(true);
			// }
		} catch (Exception e) {
			((ServerSender) serverSender).closeAll();
			serverSender.interrupt();
		}
		super.onDestroy();
	}
}
