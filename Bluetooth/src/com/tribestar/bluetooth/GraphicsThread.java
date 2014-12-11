package com.tribestar.bluetooth;

import android.view.View;

public class GraphicsThread extends Thread {

	private View view;
	private long sleepTime;
	private boolean running;
	private byte[] buffer;

	GraphicsThread(View view, long sleepTime) {
		this.view = view;
		this.sleepTime = sleepTime;
		this.running = true;
		this.buffer = new byte[0];
	}

	protected synchronized void setRunning(boolean b) {
		running = b;
	}

	protected synchronized boolean isRunning() {
		return running;
	}

	public void run() {

		while (running) {

			// view.generateSnow();
			// view.move();
			// view.checkForHit();

			try {
				// view.draw();
			} catch (NullPointerException e) {
				// V.log("Nullpointer exception when drawing.");
			}

			// Wait for some time
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
			}
		}
	}

	void requestExitAndWait() {
		running = false;
		try {
			this.join();
		} catch (InterruptedException ie) {
		}
	}

	void onWindowResize(int w, int h) {
		// Deal with change in surface size
	}

	void postByteBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
}
