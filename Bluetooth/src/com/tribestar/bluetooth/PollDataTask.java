package com.tribestar.bluetooth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

class PollDataTask extends Thread {
	private String filename;
	InputStream is = null;
	OutputStream os = null;
	PrintWriter pw = null;
	BluetoothSocket socket = null;
	int pulse;

	protected PollDataTask(MainActivity activity, BluetoothDevice noninDevice, String filename) {
		this.activity = activity;
		this.noninDevice = noninDevice;
		this.adapter = BluetoothAdapter.getDefaultAdapter();
		this.filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
	}

	/**
	 * A simple example: poll one frame of data from the Nonin sensor
	 */
	@Override
	public void run() {
		String output = "";
		V.log("Starting background");

		// an ongoing discovery will slow down the connection
		adapter.cancelDiscovery();

		try {
			// connect the bluetooth socket
			socket = noninDevice.createRfcommSocketToServiceRecord(STANDARD_SPP_UUID);
			socket.connect();
			V.log("Connected");

			// socket streams
			is = socket.getInputStream();
			os = socket.getOutputStream();
			// file stream
			pw = new PrintWriter(new FileOutputStream(new File(filename)));
			pw.write("Pulse\tPleth\n");

			V.log("Streamed");
			// os.write(FORMAT);
			os.write(SELECT_FORMAT);
			os.flush();
			byte[] reply = new byte[1];
			is.read(reply);
			V.log("REPLIED");

			// see if we received an ack
			if (reply[0] == ACK) {
				V.log("ACK");
				// byte[] frame = new byte[4];
				// this -obsolete- format specifies 4 bytes per frame
				int msb = 0;
				int lsb = 0;
				for (int packet = 1; true; packet++) {
					for (int i = 1; i <= 25; i++) {
						byte[] frame = new byte[FRAME_SIZE];
						is.read(frame);
						int value0 = unsignedByteToInt(frame[0]); // 01
						int value1 = unsignedByteToInt(frame[1]); // STATUS
						if (checkSync(value1)) { // check if we are out of sync
							if (i != 1) {
								V.log("WAS OUT OF SYNC..CORRECTING...");
							}
							// resync
							i = 1;
							continue;
						}

						int value2 = unsignedByteToInt(frame[2]); // PLETH
						int value3 = unsignedByteToInt(frame[3]); // PRMSB
						int value4 = unsignedByteToInt(frame[4]); // CHK

						// frame 20 and 21 is MSB and LSB - pulses
						if (i == 20 || i == 21) {
							if (!checkStatus(value1))
								continue;
							output = packet + "." + i + ": " + value3;
							// V.log(output);
						}

						if (i == 20) {
							// mask the most significant bits
							msb = value3 & MSB_BITMASK;
						} else if (i == 21) {
							// mask the least significant bits
							lsb = value3 & LSB_BITMASK;
							// shift the most significant bits 7 bits to the
							// left
							msb = msb << 7;
							// add lsb and msb together to forme the pulse
							pulse = lsb + msb;
						}

						if (pulse > 0) {
							int pleth = value2;
							// String out = pulse + "\t" + pleth + "\n";
							V.log("pulse: " + pulse + " pleth: " + pleth);
							writeToScreen("Pulse:" + pulse);
							pw.write(pulse + "\t" + pleth + "\n");
							pw.flush();
						}

					}
				}
			}
		} catch (Exception e) {
			output = e.getMessage();
		} finally {
			try {
				if (socket != null)
					is.close();
				os.close();
				socket.close();
				pw.close();

			} catch (Exception e) {
			}
		}

		// return output;
	}

	private static boolean checkSync(int status) {
		if ((status & 0b1) == 1) {// sync bit is first bit
			return true;
		} else
			return false;
	}

	private static boolean checkStatus(int status) {

		if ((status & 0b111100) > 0) {// status is bad
			V.log("status masked: " + (status & 0b111100));
			return false;
		} else
			return true;
	}

	public void cancel() {
		try {
			is.close();
			os.close();
			socket.close();
			pw.close();

		} catch (Exception e) {
		}
	}

	private void notifyUIThread(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				V.log("Toasted the UI thread with: " + message);
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void writeToScreen(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				// write to the textview, using the UI-thread
				TextView view = (TextView) activity.findViewById(R.id.textView2);
				view.setText(message);
			}
		});
	}

	// The byte sequence to set sensor to a basic, and obsolete, format
	private static final byte[] FORMAT = { 0x44, 0x31 };
	// Select format 2:
	private static final byte[] SELECT_FORMAT = { 0x02, 0x70, 0x04, 0x02, 0x02, 0x00, (byte) 0x78, 0x03 };
	private static final byte FRAME_SIZE = 5;
	private static final byte ACK = 0x06; // ACK from Nonin sensor
	private static final int MSB_BITMASK = 3;
	private static final int LSB_BITMASK = 127;

	private static final UUID STANDARD_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private MainActivity activity;
	private BluetoothDevice noninDevice;
	private BluetoothAdapter adapter;

	// NB! Java does not support unsigned types
	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}
