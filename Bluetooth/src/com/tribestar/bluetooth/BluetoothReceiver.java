package com.tribestar.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class BluetoothReceiver extends Thread {
	private String filename;
	private final BluetoothServerSocket mmServerSocket;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	private BluetoothSocket mmSocket;
	private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	InputStream tmpIn = null;
	OutputStream tmpOut = null;
	private Activity activity;

	public BluetoothReceiver(String filename, Activity activity) {
		log("Receiver started instantiation");
		this.activity = activity;
		this.filename = filename;
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothServerSocket tmp = null;
		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Lab3", DEFAULT_UUID);
			log("Receiver created socket");
		} catch (IOException e) {
			log("IOException: ");
			e.printStackTrace();
		}
		mmServerSocket = tmp;

	}

	public void run() {
		BluetoothSocket socket = null;
		log("Receiver entered run()");
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				socket = mmServerSocket.accept();
				log("Socket successfully found");

				mmSocket = socket;
				InputStream tmpIn = null;
				OutputStream tmpOut = null;

				// Get the input and output streams, using temp objects because
				// member streams are final
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();

				mmInStream = tmpIn;
				mmOutStream = tmpOut;
				log("All streams created");
			} catch (Exception e) {
				notifyUIThread("Thread Cancelled!");
				e.printStackTrace();
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection
				try {
					manageConnectedSocket(socket);

				} catch (InterruptedException e) {
					notifyUIThread("Thread Cancelled!");
					cancel();
					break;
				}

			}
		}
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) {
		}
	}

	private void manageConnectedSocket(BluetoothSocket socket) throws InterruptedException {
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity
				// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();
				notifyUIThread("Hello I am le bluetooth");
			} catch (IOException e) {
				break;
			}
		}

	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
		} catch (IOException e) {
		}
	}

	private void saveToFile() {

	}

	private void notifyUIThread(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				log("Toasted the UI thread with: " + message);
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void log(String message) {
		Log.d("Lab3", message);
	}

}
