package se.tribestar.mobil14.sensorbloom;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerHandler implements SensorEventListener {
	float accelX;
	float accelY;
	float accelZ;
	double shakeStartTime = Float.MAX_VALUE;
	boolean shaking;
	boolean shakeCompleted;

	double checkShakeInterval = 100; // Check for shakes every 100ms
	double lastUpdate = 0f;

	private static final int SHAKE_THRESHOLD = 400;

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
		if (!shakeCompleted) {
			shaking = checkShake(event.values[0], event.values[1], event.values[2]);
			if (shaking) { // Check if shaked for more than 1 s
				if (System.currentTimeMillis() - shakeStartTime > 1000) {
					shakeCompleted = true;
					V.log("EXPLODE!!!!");
				}
			}
		}
		accelX = event.values[0];
		accelY = event.values[1];
		accelZ = event.values[2];

	}

	private boolean checkShake(float newX, float newY, float newZ) {
		double curTime = System.currentTimeMillis();
		if ((curTime - lastUpdate) > 80) {
			double diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;
			double speed = Math.abs(newX + newY + newZ - accelX - accelY - accelZ) / diffTime * 10000;
			if (speed > SHAKE_THRESHOLD) {
				V.log("shake!! speed: " + speed);
				if (!shaking) {
					shakeStartTime = curTime;
					V.log("Shake for first time");
				}
				return true;
			} else {
				return false;
			}
		} else {
			return shaking;
		}
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
