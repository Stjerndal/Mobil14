package se.tribestar.mobil14.lab2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NMMView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	private boolean hasSurface;

	public final int X_RESOLUTION, Y_RESOLUTION;

	private final Drawable whiteMan, blackMan; // representations of the
												// actual images
	private final Drawable board;

	private ArrayList<Man> whiteMen, blackMen;

	private GraphicsThread graphicsThread;
	private Handler handler = new Handler();

	public NMMView(Context context, int xRes, int yRes) {
		super(context);

		// SurfaceView specific initialization
		holder = getHolder();
		holder.addCallback(this);
		hasSurface = false;

		X_RESOLUTION = xRes;
		Y_RESOLUTION = yRes;

		// Create movable icons using the specified images
		whiteMan = context.getResources().getDrawable(R.drawable.white_man);
		blackMan = context.getResources().getDrawable(R.drawable.black_man);

		board = context.getResources().getDrawable(R.drawable.nmm_board);

		// TODO HERE -- INITIALIZE ALL MEN

		whiteMen = new ArrayList<Man>();
		blackMen = new ArrayList<Man>();

		initGame();
	}

	private void initGame() {
		whiteMen.clear();
		blackMen.clear();

		// slMovable.setPosition(X_RESOLUTION / 2, Y_RESOLUTION -
		// slMovable.getIconBounds().height() - 150);
		// TODO HERE -- SET POSITION OF ALL MEN

		// NB!! Need this for capturing key events
		setFocusable(true);
		requestFocus();
	}

	public void resume() {
		if (graphicsThread == null) {
			Log.i("BounceSurfaceView", "resume");
			graphicsThread = new GraphicsThread(this, 20); // 20 ms between
															// updates
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("TouchView.onTouchEvent", "event = " + event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// int x = (int) event.getX();
			// TODO HERE -- HANDLE INPUT

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// TODO HERE -- HANDLE INPUT

		}

		return false;
	}

	protected void move() {
		// TODO HERE -- MOVE LOGIC
	}

	protected void draw() {
		// TO DO: Draw on an off screen Bitmap before
		// calling holder.lockCanvas()

		Canvas canvas = holder.lockCanvas();
		{
			// Paint the background
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.board_bg));
			canvas.drawPaint(paint);

			// Draw the movables
			for (Man m : whiteMen) {
				m.draw(canvas);
			}
			for (Man m : blackMen) {
				m.draw(canvas);
			}

			board.setBounds(new Rect((int) 0, (int) 50, (int) 0 + board.getIntrinsicWidth(), (int) 50
					+ board.getIntrinsicHeight()));
			board.draw(canvas);

			if (graphicsThread.isRunning() == false) {
				paint.setColor(Color.RED);
				paint.setTextAlign(Align.CENTER);
				paint.setTextSize(42);
				paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
				canvas.drawText("Game Over", (float) X_RESOLUTION / 2, (float) Y_RESOLUTION / 2, paint);
			}
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
