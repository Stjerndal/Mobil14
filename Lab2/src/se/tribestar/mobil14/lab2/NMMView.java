package se.tribestar.mobil14.lab2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
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
	private final Drawable boardPic;

	private Sprite boardSprite;

	private ArrayList<Sprite> whiteMen, blackMen;

	private GraphicsThread graphicsThread;
	private Handler handler = new Handler();

	private NMMRules rules;

	private Sprite clickedSprite;

	private boolean selectedMarker;
	private boolean selectedMove;

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

		boardPic = context.getResources().getDrawable(R.drawable.nmm_board);
		boardSprite = new Sprite(0, 0, boardPic, X_RESOLUTION, Y_RESOLUTION, 0.9f);
		boardSprite.setPositionCenter();

		// TODO HERE -- INITIALIZE ALL MEN

		whiteMen = new ArrayList<Sprite>();
		blackMen = new ArrayList<Sprite>();

		initGame();
	}

	private void initGame() {
		whiteMen.clear();
		blackMen.clear();

		selectedMarker = false;
		selectedMove = false;
		rules = new NMMRules();

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
			int x = (int) event.getX();
			int y = (int) event.getY();

			ArrayList<Sprite> listToCheck;
			if (rules.getPlayerInTurn() == rules.WHITE_MARKER)
				listToCheck = whiteMen;
			if (rules.getPlayerInTurn() == rules.BLACK_MARKER)
				listToCheck = blackMen;

			bool isSelectingMarker = false;
			for (Sprite sprite : listToCheck) {
				if (sprite.isWithinBounds(x, y)) {
					clickedSprite = sprite;
					isSelectingMarker = true;
					selectedMarker = true;
					// TODO -- Insert some animation or something to let the
					// user know it selected a marker
					break;
				}
			}

			// check to see if we want to place a marker somewhere
			if (!isSelectingMarker && selectedMarker) {
				// TODO
			}

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

			// boardPic.setBounds(new Rect((int) 0, (int) 50, (int) 0 +
			// boardPic.getIntrinsicWidth(), (int) 50
			// + boardPic.getIntrinsicHeight()));
			// boardPic.draw(canvas);
			boardSprite.draw(canvas);

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
