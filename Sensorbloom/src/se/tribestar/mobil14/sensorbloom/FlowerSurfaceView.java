package se.tribestar.mobil14.sensorbloom;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FlowerSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	private boolean hasSurface;

	public final int X_RESOLUTION, Y_RESOLUTION;

	private Sprite flowerSprite;

	// Flags indicating motion of slMovable
	private boolean movingRight, movingLeft;
	private float slVelocity, flakeVelocity;

	AccelerometerHandler accHandler;

	private GraphicsThread graphicsThread;
	private Handler handler = new Handler();
	private Flower flower;

	private Random rand = new Random();

	public FlowerSurfaceView(Context context, int xRes, int yRes) {
		super(context);

		// SurfaceView specific initialization
		holder = getHolder();
		holder.addCallback(this);
		hasSurface = false;

		X_RESOLUTION = xRes;
		Y_RESOLUTION = yRes;

		flower = new Flower(context, xRes, yRes);

		accHandler = new AccelerometerHandler(context);

		slVelocity = 4.0F;
		flakeVelocity = 4.0F;

		initGame();
	}

	private void initGame() {
		movingRight = movingLeft = false;

		setFocusable(true);
		requestFocus();
	}

	public void resume() {
		if (graphicsThread == null) {
			Log.i("BounceSurfaceView", "resume");
			graphicsThread = new GraphicsThread(this, 2); // 30 fps
			if (hasSurface) {
				graphicsThread.start();
			}
		}
	}

	public void pause() {
		if (graphicsThread != null) {
			Log.i("BounceSurfaceView", "pause");
			graphicsThread.requestExitAndWait();
			graphicsThread = null;
		}
	}

	protected void move() {

	}

	// public boolean onTouchEvent(MotionEvent event) {
	// flower.explode();
	// return false;
	// }

	protected void draw(float deltaTime) {

		Canvas canvas = holder.lockCanvas();
		{
			// Paint the background
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.flower_bg));
			canvas.drawPaint(paint);

			if (accHandler.getShakeCompleted())
				flower.explode();
			flower.draw(accHandler.getAccelX(), canvas, deltaTime);
		}
		holder.unlockCanvasAndPost(canvas);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		hasSurface = true;
		if (graphicsThread != null) {
			graphicsThread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		pause();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (graphicsThread != null) {
			graphicsThread.onWindowResize(w, h);
		}
	}
}
