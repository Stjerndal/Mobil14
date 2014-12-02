package se.tribestar.mobil14.lab2;

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

	private final Drawable whiteMarker, blackMarker; // representations of the
	// actual images
	private final Drawable boardPic;

	private Sprite boardSprite;

	private Node[] nodes;

	private GraphicsThread graphicsThread;
	private Handler handler = new Handler();

	private NMMRules rules;

	private int selectedMarker;
	private int selectedDestination;

	private int whiteMarkersToPlace;
	private int blackMarkersToPlace;

	private boolean hasSelectedMarker;
	private boolean hasSelectedDestination;

	private boolean placePhase;
	private boolean removePhase;

	public NMMView(Context context, int xRes, int yRes) {
		super(context);

		// SurfaceView specific initialization
		holder = getHolder();
		holder.addCallback(this);
		hasSurface = false;

		X_RESOLUTION = xRes;
		Y_RESOLUTION = yRes;

		// Create movable icons using the specified images
		whiteMarker = context.getResources().getDrawable(R.drawable.white_man);
		blackMarker = context.getResources().getDrawable(R.drawable.black_man);

		boardPic = context.getResources().getDrawable(R.drawable.nmm_board);
		boardSprite = new Sprite(0, 0, boardPic, X_RESOLUTION, Y_RESOLUTION, 0.9f);
		boardSprite.setPositionCenter();

		nodes = NodeRectCalculator.generateNodes(boardSprite.getPosX(), boardSprite.getPosY(), boardSprite.getWidth());

		initGame();
	}

	private void initGame() {

		for (Node node : nodes) {
			node.removePlayer();
		}

		hasSelectedMarker = false;
		hasSelectedDestination = false;
		placePhase = true;
		removePhase = false;

		whiteMarkersToPlace = 9;
		blackMarkersToPlace = 9;
		rules = new NMMRules();

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

			for (int i = 0; i < nodes.length; i++) {
				Node node = nodes[i];
				if (node.isWithinBounds(x, y)) {
					if (placePhase) {
						if (node.getPlayerColor() == rules.EMPTY_SPACE) {
							selectedDestination = i;
							hasSelectedDestination = true;
						}

					} else if (removePhase) {

						if (node.hasPlayer() && node.getPlayerColor() != rules.getPlayerInTurn()) {
							selectedDestination = i;
							hasSelectedDestination = true;
						}

					} else {
						if (node.getPlayerColor() == rules.getPlayerInTurn()) {
							selectedMarker = i;
							hasSelectedMarker = true;
						} else if (node.getPlayerColor() == rules.EMPTY_SPACE && hasSelectedMarker) {
							selectedDestination = i;
							hasSelectedDestination = true;
						}

						// TODO -- Insert some animation or something to let the
						// user know it made som action
						break;
					}

				}
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// TODO HERE -- HANDLE INPUT

		}

		return false;
	}

	protected void move() {

		if (placePhase && hasSelectedDestination) {
			if (rules.legalMove(selectedDestination, rules.UNPLACED, rules.getPlayerInTurn())) {
				int x = nodes[selectedDestination].getRect().left;
				int y = nodes[selectedDestination].getRect().top;
				int playerColor = rules.getPlayerInTurn();

				if (playerColor == rules.WHITE_MOVES && whiteMarkersToPlace > 0) {
					whiteMarkersToPlace--;
					nodes[selectedDestination].setPlayer(
							new Sprite(x, y, whiteMarker, X_RESOLUTION, Y_RESOLUTION, 0.1f), playerColor);
				} else if (playerColor == rules.BLACK_MOVES && blackMarkersToPlace > 0) {
					blackMarkersToPlace--;
					nodes[selectedDestination].setPlayer(
							new Sprite(x, y, blackMarker, X_RESOLUTION, Y_RESOLUTION, 0.1f), playerColor);
				} else if (whiteMarkersToPlace > 0 && blackMarkersToPlace > 0) {
					placePhase = false;
					hasSelectedDestination = false;
					return;
				} else {
					hasSelectedDestination = false;
					return;
				}

			}

			hasSelectedDestination = false;

		} else if (hasSelectedMarker && hasSelectedDestination) {

			if (rules.legalMove(selectedMarker, selectedDestination, rules.getPlayerInTurn())) {
				int x = nodes[selectedDestination].getRect().left;
				int y = nodes[selectedDestination].getRect().top;
				int playerColor = rules.getPlayerInTurn();
				nodes[selectedMarker].removePlayer();
				nodes[selectedDestination].setPlayer(new Sprite(x, y, blackMarker, X_RESOLUTION, Y_RESOLUTION, 0.1f),
						playerColor);
			}

			hasSelectedMarker = false;
			hasSelectedDestination = false;
		} else if (removePhase && hasSelectedDestination) {

			// TODO

			hasSelectedDestination = false;
		}
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

			// boardPic.setBounds(new Rect((int) 0, (int) 50, (int) 0 +
			// boardPic.getIntrinsicWidth(), (int) 50
			// + boardPic.getIntrinsicHeight()));
			// boardPic.draw(canvas);
			boardSprite.draw(canvas);

			// Draw the movables
			for (Node node : nodes) {
				try {
					node.getSprite().draw(canvas);
				} catch (Exception e) {

				}
			}

			if (graphicsThread.isRunning() == false) {
				paint.setColor(Color.RED);
				paint.setTextAlign(Align.CENTER);
				paint.setTextSize(42);
				paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
				canvas.drawText("Game Over", (float) X_RESOLUTION / 2, (float) Y_RESOLUTION / 2, paint);
			}

			// TODO Draw phase-dependant messages
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
