package se.tribestar.mobil14.sensorbloom;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class for handling device accelerometer. Based on suggestions from the book
 * Beginning Android games.
 * 
 */
public class AccelerometerHandler implements SensorEventListener {
	float accelX;
	float accelY;
	float accelZ;
	double shakeStartTime = Float.MAX_VALUE; // When a shake is started
	boolean shaking; // are we shaking right now?
	boolean shakeCompleted; // have we completed a shake?

	// Check for shakes every 100ms
	private static final double checkShakeInterval = 100;
	double lastUpdate = 0f;

	private static final float F = 0.75f; // Filterfaktorn
	// Min speed to start shaking
	private static final int SHAKE_START_SPEED = 300;
	// Max speed to stop shaking
	private static final int SHAKE_STOP_SPEED = 50;

	public AccelerometerHandler(Context context) {
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		float newX = filteredValue(accelX, event.values[0]);
		float newY = filteredValue(accelY, event.values[1]);
		float newZ = filteredValue(accelZ, event.values[2]);

		if (!shakeCompleted) {
			shaking = checkShake(newX, newY, newZ);
			if (shaking) { // Check if shaked for more than 1 s
				if (System.currentTimeMillis() - shakeStartTime > 1000) {
					shakeCompleted = true;
					V.log("EXPLODE!!!!");
				}
			}
		}

		accelX = newX;
		accelY = newY;
		accelZ = newZ;

		// accelX = event.values[0];
		// accelY = event.values[1];
		// accelZ = event.values[2];

	}

	private float filteredValue(float prevValue, float sensorValue) {
		return F * prevValue + (1 - F) * sensorValue;
	}

	/**
	 * Check for shake. Field 'shaking' is updated once every checkShakeInterval
	 * ms.
	 */
	private boolean checkShake(float newX, float newY, float newZ) {
		double curTime = System.currentTimeMillis();
		if ((curTime - lastUpdate) > checkShakeInterval) {
			double diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;
			// shakespeed:
			double speed = Math.abs(newX + newY + newZ - accelX - accelY - accelZ) / diffTime * 10000;
			// enough speed:
			if (speed > SHAKE_START_SPEED) {
				V.log("shake!! speed: " + speed + ": " + shaking);
				if (!shaking) { // if beginning a new shake
					shakeStartTime = curTime;
					V.log("Shake for first time");
				}
				return true;
				// not enough speed:
			} else {
				// make it easier to continously shake.
				if (shaking && speed < SHAKE_STOP_SPEED) {
					return false;
				} else
					return shaking;
			}
		} else {
			return shaking;
		}
	}

	public boolean getShakeCompleted() {
		return shakeCompleted;
	}

	public float getAccelX() {
		return accelX;
	}

	public float getAccelY() {
		return accelY;
	}

	public float getAccelZ() {
		return accelZ;
	}
}
