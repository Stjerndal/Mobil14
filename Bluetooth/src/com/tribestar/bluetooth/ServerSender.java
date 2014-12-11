package com.tribestar.bluetooth;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ServerSender extends Thread {
	private String filename;
	private String address = "192.168.1.2";
	private int port = 11111;
	private Activity activity;

	BufferedReader in;
	PrintWriter out;
	BufferedReader stdIn;

	public ServerSender(String filename, Activity activity) {
		log("Starting serverSender");
		this.filename = filename;
		this.activity = activity;

	}

	public void run() {
		log("ServerSender run()");
		String message = "I am a remote device, hear me roar.";
		try {

			Socket socket = new Socket(address, port);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));

			log("ServerSender established connection with server at: " + address);
			// while ((message = stdIn.readLine()) != null) {

			out.println(message);
			out.flush();
			notifyUIThread("ServerSender completed transmission");

			// }

		} catch (Exception ex) {
			notifyUIThread("Couldn't establish connection to server");
			closeAll();
			ex.printStackTrace();
		}
		closeAll();
	}

	public void closeAll() {
		try {
			out.close();
			in.close();
			stdIn.close();
			notifyUIThread("ServerSender cancelled");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void makeTestFile() {
		FileOutputStream outputStream;
		String string = "";
		try {
			outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
			int pleth = 0;
			int pulse = 0;
			outputStream.write(("" + System.currentTimeMillis() + "\n").getBytes());
			outputStream.write("Pleth\tPulse".getBytes());
			int i = 0;
			while (i < 10) {

				i++;
			}
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void log(String message) {
		Log.d("Lab3", message);
	}

	private void notifyUIThread(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				log("Toasted the UI thread with: " + message);
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}