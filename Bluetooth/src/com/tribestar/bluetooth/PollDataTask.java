package com.tribestar.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;

class PollDataTask extends Thread {
	private String filename;
	InputStream is = null;
	OutputStream os = null;
	BluetoothSocket socket = null;

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
			socket = noninDevice.createRfcommSocketToServiceRecord(STANDARD_SPP_UUID);
			socket.connect();
			V.log("Connected");

			is = socket.getInputStream();
			os = socket.getOutputStream();

			V.log("Streamed");
			// os.write(FORMAT);
			os.write(SELECT_FORMAT);
			os.flush();
			byte[] reply = new byte[1];
			is.read(reply);
			V.log("REPLIED");

			if (reply[0] == ACK) {
				V.log("ACK");
				// byte[] frame = new byte[4];
				// this -obsolete- format specifies 4 bytes per frame
				for (int packet = 1; true; packet++) {
					for (int i = 1; i <= 25; i++) {
						byte[] frame = new byte[FRAME_SIZE];
						is.read(frame);
						int value0 = unsignedByteToInt(frame[0]); // 01
						int value1 = unsignedByteToInt(frame[1]); // STATUS
						int value2 = unsignedByteToInt(frame[2]); // PLETH
						int value3 = unsignedByteToInt(frame[3]); // PRMSB
						int value4 = unsignedByteToInt(frame[4]); // CHK

						// output = packet + "." + i + ":  " + value0 + "; " +
						// value1 + "; " + value2 + "; " + value3
						// + "; " + value4 + "\r\n";
						// if (i == 18 || i == 24) {
						if (i == 3) {
							output = packet + "." + i + ": " + value3;
							V.log(output);
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

			} catch (Exception e) {
			}
		}

		// return output;
	}

	public void cancel() {
		try {
			is.close();
			os.close();
			socket.close();

		} catch (Exception e) {
		}
	}

	// The byte sequence to set sensor to a basic, and obsolete, format
	private static final byte[] FORMAT = { 0x44, 0x31 };
	// Select format 2:
	private static final byte[] SELECT_FORMAT = { 0x02, 0x70, 0x04, 0x02, 0x02, 0x00, (byte) 0x78, 0x03 };
	private static final byte FRAME_SIZE = 5;
	private static final byte ACK = 0x06; // ACK from Nonin sensor

	private static final UUID STANDARD_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private MainActivity activity;
	private BluetoothDevice noninDevice;
	private BluetoothAdapter adapter;

	// NB! Java does not support unsigned types
	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}
