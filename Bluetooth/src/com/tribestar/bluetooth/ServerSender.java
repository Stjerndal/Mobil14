package com.tribestar.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ServerSender extends Thread {
	private String filename;
	private String address = "130.229.131.255";
	private int port = 11111;
	private Activity activity;
	private Socket socket;

	BufferedReader in;
	PrintWriter out;
	BufferedReader stdIn;

	public ServerSender(String filename, Activity activity) {
		log("Starting serverSender");
		this.filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
		this.activity = activity;

	}

	public void run() {
		log("ServerSender run()");
		try {

			this.socket = new Socket(address, port);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));

			log("ServerSender established connection with server at: " + address);
			// while ((message = stdIn.readLine()) != null) {

			makeTestFile();
			sendFile();
			notifyUIThread("ServerSender completed transmission");

			// }

		} catch (Exception ex) {
			notifyUIThread("Couldn't establish connection to server");
			closeAll();
			ex.printStackTrace();
		}
		closeAll();
	}

	public void sendFile() {
		try {
			log("Begin transmitting file to server");
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			byte[] bytes = new byte[(int) file.length()];

			int count;
			while ((count = bis.read(bytes)) > 0) {
				out.write(bytes, 0, count);

				System.out.println(count);
			}
			out.flush();
			out.close();
			fis.close();
			bis.close();

			log("Successful transmission of file to server");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void closeAll() {
		try {
			out.close();
			in.close();
			stdIn.close();
			socket.close();
			notifyUIThread("ServerSender closed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void makeTestFile() {

		// Get the directory for the user's public pictures directory.
		File file = new File(filename);

		FileOutputStream outputStream;
		String string = "";
		try {
			log("Making testfile");
			outputStream = new FileOutputStream(filename);
			Random dice = new Random();

			outputStream.write(("" + System.currentTimeMillis() + "\n").getBytes());
			outputStream.write("Pleth\tPulse\n".getBytes());
			int i = 0;
			while (i < 10) {
				int pleth = dice.nextInt(200);
				int pulse = dice.nextInt(200);
				outputStream.write((pleth + "\t" + pulse + "\n").getBytes());
				log(pleth + "\t" + pulse + "\n");
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