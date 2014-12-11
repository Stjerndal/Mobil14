package se.tribestar.mobil14.sensorbloom;

class GraphicsThread extends Thread {

	private FlowerSurfaceView view;
	private long sleepTime;
	private boolean running;

	GraphicsThread(FlowerSurfaceView view, long sleepTime) {
		this.view = view;
		this.sleepTime = sleepTime;
		this.running = true;
	}

	protected synchronized void setRunning(boolean b) {
		running = b;
	}

	protected synchronized boolean isRunning() {
		return running;
	}

	public void run() {

		while (running) {

			view.move();
			view.draw();

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
}