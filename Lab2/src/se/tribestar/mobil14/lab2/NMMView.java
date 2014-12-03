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

	public static int X_RESOLUTION, Y_RESOLUTION;

	// representations of the actual images
	public static Drawable whiteMarker, blackMarker;
	private final Drawable boardPic;

	private Sprite boardSprite;

	private Node[] nodes;

	private GraphicsThread graphicsThread;
	private Handler handler = new Handler();

	private NMMRules rules;

	private int selectedMarker;
	private int selectedDestination;

	public static int whiteMarkersToPlace;
	public static int blackMarkersToPlace;

	public static boolean hasSelectedMarker;
	public static boolean hasSelectedDestination;

	public static boolean placePhase;
	public static boolean removePhase;

	private Context context;

	public NMMView(Context context, int xRes, int yRes) {
		super(context);
		this.context = context;

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

	/**
	 * This method will initiate a completely new game
	 */
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

	// life cycle method
	public void resume() {

		if (graphicsThread == null) {
			Log.i("BounceSurfaceView", "resume");
			graphicsThread = new GraphicsThread(this, 20); // 20 ms between
															// updates
			if (hasSurface) {
				graphicsThread.start();
			}
		}
		initGame();
		loadState();
		updateNodes();
	}

	// life cycle method
	public void pause() {
		if (graphicsThread != null) {
			Log.i("BounceSurfaceView", "pause");
			graphicsThread.requestExitAndWait();
			graphicsThread = null;
		}
		saveState();
	}

	// start a new game
	public void restart() {
		V.log("restart");
		graphicsThread = new GraphicsThread(this, 20); // 20 ms between
		// updates
		if (hasSurface) {
			graphicsThread.start();
		}
		initGame();
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
					// If we're placing we want to check for a destination
					if (placePhase && !removePhase) {
						if (node.getPlayerColor() == NMMRules.EMPTY_SPACE) {
							selectedDestination = i;
							hasSelectedDestination = true;
						}
						// Else if we're trying to remove, check for a marker
					} else if (removePhase) {
						// make sure that the node has a marker and that it is
						// the right color
						if (node.hasPlayer() && node.getPlayerColor() == rules.getPlayerInTurn()) {
							selectedMarker = i;
							hasSelectedMarker = true;
						}

					} else {
						// else if we're moving
						if (node.getPlayerColor() == rules.getPlayerInTurn()) {
							// if we clicked one of our own markers we want it
							// to be selected
							selectedMarker = i;
							hasSelectedMarker = true;
						} else if (!node.hasPlayer() && hasSelectedMarker && !hasSelectedDestination) {
							// else if we already selected one of our markers,
							// check for a destination
							selectedDestination = i;
							hasSelectedDestination = true;
						}
						break;
					}

				}
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// TODO HERE -- HANDLE INPUT

		}

		return false;
	}

	/**
	 * Handles all logic
	 */
	protected void move() {
		// Check if we're still placing nodes and has selected a destination
		// node for placement
		if (placePhase && hasSelectedDestination && !removePhase) {
			int playerInTurn = rules.getPlayerInTurn();
			// if successfully placed a node at the given spot
			if (rules.legalMove(selectedDestination, NMMRules.UNPLACED, playerInTurn)) {
				// check if it's part of 3-in-row
				removePhase = rules.remove(selectedDestination);

				if (playerInTurn == NMMRules.WHITE_MOVES && whiteMarkersToPlace > 0) {
					// if we had markers left, set a marker
					whiteMarkersToPlace--;
					nodes[selectedDestination].setPlayer(playerInTurn);
				} else if (playerInTurn == NMMRules.BLACK_MOVES && blackMarkersToPlace > 0) {
					// if we had markers left, set a marker
					blackMarkersToPlace--;
					nodes[selectedDestination].setPlayer(playerInTurn);
				} else {
					hasSelectedDestination = false;
					return;
				}
				// check if we're done with the placing phase
				if (whiteMarkersToPlace <= 0 && blackMarkersToPlace <= 0) {
					placePhase = false;
					hasSelectedDestination = false;
					return;
				}

			}
			hasSelectedDestination = false;
		}
		// Check whether we are in the move-phase and has selected from and
		// destination nodes
		else if (hasSelectedMarker && hasSelectedDestination && !removePhase) {
			int playerInTurn = rules.getPlayerInTurn();
			if (rules.legalMove(selectedDestination, selectedMarker, playerInTurn)) {

				// if the move was successful we want to remove our player from
				// the node
				nodes[selectedMarker].removePlayer();
				// we also want to add a player-marker to the destination
				nodes[selectedDestination].setPlayer(playerInTurn);
				// then check if it was part of 3-in-row
				removePhase = rules.remove(selectedDestination);
			}

			hasSelectedMarker = false;
			hasSelectedDestination = false;
		}
		// if we're trying to remove a node and has a marker selected
		else if (removePhase && hasSelectedMarker) {

			if (rules.getPlayerInTurn() == NMMRules.WHITE_MOVES) {
				if (rules.remove(selectedMarker, NMMRules.WHITE_MARKER)) {
					// Note: the players turn's have already switched so the
					// above statement is infact correct
					// if we can remove it, then do so
					nodes[selectedMarker].removePlayer();
					removePhase = false;
				}
			}
			if (rules.getPlayerInTurn() == NMMRules.BLACK_MOVES) {
				if (rules.remove(selectedMarker, NMMRules.BLACK_MARKER)) {
					// Note: the players turn's have already switched so the
					// above statement is infact correct
					// if we can remove it, then do so
					nodes[selectedMarker].removePlayer();
					removePhase = false;
				}
			}

			hasSelectedMarker = false;
		}

	}

	protected void draw() {
		// TO DO: Draw on an off screen Bitmap before
		// calling holder.lockCanvas()
		// try {

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

			paint.setColor(Color.RED);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(42);
			paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));

			int winner = checkGameOver();
			// If the game is over and we have a winner
			if ((winner == NMMRules.WHITE_MOVES || winner == NMMRules.BLACK_MOVES) && !placePhase) {

				if (winner == NMMRules.WHITE_MOVES) {
					showStatusText("GAME OVER! White wins!", canvas, paint);
				}

				if (winner == NMMRules.BLACK_MOVES) {
					showStatusText("GAME OVER! Black wins!", canvas, paint);
				}

			} else {
				// If we are still placing markers
				if (placePhase && !removePhase) {
					if (rules.getPlayerInTurn() == NMMRules.WHITE_MOVES) {
						showStatusText("White's turn to place (" + whiteMarkersToPlace + " left)", canvas, paint);
					}
					if (rules.getPlayerInTurn() == NMMRules.BLACK_MOVES) {
						showStatusText("Blacks's turn to place (" + blackMarkersToPlace + " left)", canvas, paint);
					}
				}
				// If we need to remove a marker
				if (removePhase) {

					if (!hasSelectedMarker) {

						if (rules.getPlayerInTurn() == NMMRules.WHITE_MOVES) {
							showStatusText("Select a white marker to remove!", canvas, paint);
						}
						if (rules.getPlayerInTurn() == NMMRules.BLACK_MOVES) {
							showStatusText("Select a black marker to remove!", canvas, paint);
						}

					} else {

					}
					// if we're looking to make a move
				} else if (!placePhase && !removePhase) {

					if (!hasSelectedMarker && !hasSelectedDestination) {
						if (rules.getPlayerInTurn() == NMMRules.WHITE_MOVES) {
							showStatusText("Select a white marker to move!", canvas, paint);
						}
						if (rules.getPlayerInTurn() == NMMRules.BLACK_MOVES) {
							showStatusText("Select a black marker to move!", canvas, paint);
						}
					} else if (hasSelectedMarker && !hasSelectedDestination) {
						showStatusText("Select a node to move to!", canvas, paint);
					}

				}
			}
			// Draw the movables
			for (Node node : nodes) {
				try {
					node.getSprite().draw(canvas);
				} catch (Exception e) {

				}
			}

		}

		holder.unlockCanvasAndPost(canvas);

		// } catch (NullPointerException e) {
		// V.log("Nullpointer Exception, graphicsthread probably being closed");
		// }
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

	/**
	 * Scalable text-messages
	 * 
	 * @param msg
	 * @param canvas
	 * @param paint
	 */
	public void showStatusText(String msg, Canvas canvas, Paint paint) {
		paint.setColor(Color.RED);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize((int) ((double) X_RESOLUTION) / 19);
		paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));

		canvas.drawText(msg, (float) X_RESOLUTION / 2, Y_RESOLUTION * 0.10f, paint);

	}

	// Update node objects based on the board in the NMMRules.
	public void updateNodes() {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].setPlayer(rules.board(i));
		}
	}

	public void loadState() {
		V.log("load state method called");
		rules.loadStateFromFile(context);
	}

	public void saveState() {
		V.log("save state method called");
		rules.saveStateToFile(context);
	}

	/**
	 * return the player who has won the game or 0 if no player won the game
	 * 
	 * @return
	 */
	public int checkGameOver() {
		if (rules.win(NMMRules.WHITE_MOVES)) {
			return NMMRules.WHITE_MOVES;
		}
		if (rules.win(NMMRules.BLACK_MOVES)) {
			return NMMRules.BLACK_MOVES;
		}

		return 0;
	}
}
